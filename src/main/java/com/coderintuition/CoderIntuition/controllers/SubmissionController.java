package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.CodeTemplateFiller;
import com.coderintuition.CoderIntuition.common.Constants;
import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.ActivityType;
import com.coderintuition.CoderIntuition.enums.SubmissionStatus;
import com.coderintuition.CoderIntuition.enums.TestStatus;
import com.coderintuition.CoderIntuition.models.*;
import com.coderintuition.CoderIntuition.pojos.request.ActivityRequestDto;
import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.pojos.request.RunRequestDto;
import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.pojos.response.TokenResponse;
import com.coderintuition.CoderIntuition.repositories.*;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Controller
public class SubmissionController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestRunRepository testRunRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    TestResultRepository testResultRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ActivityController activityController;

    @GetMapping("/submission/{token}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Submission getSubmission(@CurrentUser UserPrincipal userPrincipal, @PathVariable String token) throws Exception {
        Submission submission = submissionRepository.findByToken(token);
        if (!submission.getUser().getId().equals(userPrincipal.getId())) {
            throw new Exception("Unauthorized");
        }
        return submission;
    }

    @PutMapping("/submission/judge0callback")
    public void submissionCallback(@RequestBody JzSubmissionCheckResponseDto data) throws IOException {
        // get submission info
        JzSubmissionCheckResponseDto result = Utils.retrieveFromJudgeZero(data.getToken());

        // update the submission in the db
        Submission submission = submissionRepository.findByToken(result.getToken());
        List<TestResult> testResults = new ArrayList<>();

        // set the results of the submission
        if (result.getStatus().getId() >= 6) { // error
            submission.setStatus(SubmissionStatus.ERROR);
            String stderr = "";
            if (result.getCompileOutput() != null) {
                stderr = result.getCompileOutput();
            } else if (result.getStderr() != null) {
                stderr = result.getStderr();
            }
            submission.setStderr(Utils.formatErrorMessage(submission.getLanguage(), stderr));

        } else if (result.getStatus().getId() == 3) { // no errors
            // everything above the line is stdout, everything below is test results
            String[] split = result.getStdout().trim().split(Constants.IO_SEPARATOR);
            submission.setOutput(split[1]);
            // set status as passed at first and overwrite if any test failed
            submission.setStatus(SubmissionStatus.ACCEPTED);

            for (String str : split[1].split("\n")) {
                // test results are formatted: {test num}|{status}|{expected output}|{run output}
                // runtime error results are formatted: {test num}|{status}|{error message}
                String[] testResult = str.split("\\|");

                if (testResult.length == 4) { // no errors
                    String num = testResult[0];
                    String status = testResult[1];
                    // create the test result object to be saved into the db
                    TestResult testResultObj = new TestResult();
                    testResultObj.setSubmission(submission);
                    testResultObj.setStatus(TestStatus.valueOf(status.toUpperCase()));
                    // retrieve the test case for this test result
                    TestCase testCase = submission.getProblem().getTestCases().get(Integer.parseInt(num));
                    testResultObj.setInput(testCase.getInput());
                    testResultObj.setExpectedOutput(testResult[2]);

                    // test case failed
                    if (status.equals(TestStatus.FAILED.toString())) {
                        testResultObj.setOutput(testResult[3]);
                        // set overall submission status to failed if the status is not already ERROR
                        if (submission.getStatus() != SubmissionStatus.ERROR) {
                            submission.setStatus(SubmissionStatus.REJECTED);
                        }
                    } else {
                        testResultObj.setOutput("");
                    }

                    // add the test result to the list of test results
                    testResults.add(testResultObj);

                } else if (testResult.length == 2) { // runtime errors
                    submission.setStatus(SubmissionStatus.ERROR);
                    submission.setStderr(Utils.formatErrorMessage(submission.getLanguage(), testResult[1]));
                }
            }
            submission.setTestResults(testResults);
        }

        // save the submission into the db
        submissionRepository.save(submission);

        // send message to frontend
        this.simpMessagingTemplate.convertAndSend("/topic/submission", result.getToken());
    }

    @PostMapping("/submission")
    @PreAuthorize("hasRole('ROLE_USER')")
    public TokenResponse createSubmission(@CurrentUser UserPrincipal userPrincipal, @RequestBody RunRequestDto submissionRequestDto) throws Exception {
        // retrieve the problem
        Problem problem = problemRepository.findById(submissionRequestDto.getProblemId()).orElseThrow();

        // wrap the code into the submission template
        CodeTemplateFiller filler = CodeTemplateFiller.getInstance();
        String functionName = Utils.getFunctionName(submissionRequestDto.getLanguage(), problem.getCode(submissionRequestDto.getLanguage()));
        String primarySolution = problem.getSolutions().stream().filter(Solution::getIsPrimary).findFirst().orElseThrow().getCode(submissionRequestDto.getLanguage());
        // fill in the submission template with the arguments/return type for this test run
        String code = filler.getSubmissionCode(submissionRequestDto.getLanguage(), submissionRequestDto.getCode(), primarySolution,
            functionName, problem.getArguments(), problem.getReturnType());

        // setup stdin
        StringBuilder stdin = new StringBuilder();
        for (TestCase testCase : problem.getTestCases()) {
            stdin.append(testCase.getInput()).append("\n");
            stdin.append(Constants.IO_SEPARATOR);
        }

        // create request to JudgeZero
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(Utils.getLanguageId(submissionRequestDto.getLanguage()));
        requestDto.setStdin(stdin.toString());
        requestDto.setCallbackUrl("https://api.coderintuition.com/submission/judge0callback");
        // send request to JudgeZero
        String token = Utils.submitToJudgeZero(requestDto);

        // save submission to db
        Submission submission = new Submission();
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        submission.setUser(user);
        submission.setCode(submissionRequestDto.getCode());
        submission.setLanguage(submissionRequestDto.getLanguage());
        submission.setProblem(problem);
        submission.setToken(token);
        submissionRepository.save(submission);

        // create Activity
        ActivityRequestDto activityRequestDto = new ActivityRequestDto(
            ActivityType.SUBMIT_PROBLEM,
            problem.getId(),
            null);
        activityController.createActivity(activityRequestDto, user);

        return new TokenResponse(token);
    }
}
