package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.CodeTemplateFiller;
import com.coderintuition.CoderIntuition.common.Constants;
import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.TestStatus;
import com.coderintuition.CoderIntuition.models.*;
import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.pojos.request.RunRequestDto;
import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.SubmissionRepository;
import com.coderintuition.CoderIntuition.repositories.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class TestRunController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestRunRepository testRunRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    private final ExecutorService scheduler = Executors.newFixedThreadPool(5);

    @PostMapping("/testrun")
    public TestRun createTestRun(@RequestBody RunRequestDto runRequestDto) {
        // retrieve the problem
        Problem problem = problemRepository.findById(runRequestDto.getProblemId()).orElseThrow();

        // wrap the code into the test run template
        CodeTemplateFiller filler = CodeTemplateFiller.getInstance();
        String functionName = Utils.getFunctionName(runRequestDto.getLanguage(), problem.getCode(runRequestDto.getLanguage()));
        String primarySolution = problem.getSolutions().stream().filter(Solution::getIsPrimary).findFirst().orElseThrow().getCode(runRequestDto.getLanguage());
        // fill in the test run template with the arguments/return type for this test run
        String code = filler.getTestRunCode(runRequestDto.getLanguage(), runRequestDto.getCode(), primarySolution,
                functionName, problem.getArguments(), problem.getReturnType());

        // create request to JudgeZero
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(Utils.getLanguageId(runRequestDto.getLanguage()));
        requestDto.setStdin(runRequestDto.getInput());
        // send request to JudgeZero
        JzSubmissionCheckResponseDto result = Utils.callJudgeZero(requestDto, scheduler);

        // create the test run to be saved into the db
        TestRun testRun = new TestRun();
        testRun.setProblem(problem);
        testRun.setToken(result.getToken());
        testRun.setLanguage(runRequestDto.getLanguage());
        testRun.setCode(runRequestDto.getCode());
        testRun.setInput(runRequestDto.getInput());

        // save the results of the test run
        if (result.getStatus().getId() >= 6) { // error
            testRun.setStatus(TestStatus.ERROR);
            testRun.setExpectedOutput("");
            testRun.setOutput("");
            String stderr = "";
            if (result.getCompileOutput() != null) {
                stderr = result.getCompileOutput();
            } else if (result.getStderr() != null) {
                stderr = result.getStderr();
            }
            testRun.setStderr(Utils.formatErrorMessage(runRequestDto.getLanguage(), stderr));
            testRun.setStdout("");

        } else if (result.getStatus().getId() == 3) { // no compile errors
            // everything above the line is stdout, everything below is test results
            String[] split = result.getStdout().trim().split(Constants.IO_SEPARATOR);
            String[] testResult = split[1].split("\\|");

            if (testResult.length == 3) { // no errors
                // test results are formatted: {status}|{expected output}|{run output}
                testRun.setStatus(TestStatus.valueOf(testResult[0]));
                testRun.setExpectedOutput(testResult[1]);
                testRun.setOutput(testResult[2]);
                testRun.setStdout(split[0]);
                testRun.setStderr("");

            } else if (testResult.length == 2) { // runtime errors
                // runtime error results are formatted: {status}|{error message}
                testRun.setStatus(TestStatus.ERROR);
                testRun.setExpectedOutput("");
                testRun.setOutput("");
                testRun.setStderr(Utils.formatErrorMessage(runRequestDto.getLanguage(), testResult[1]));
                testRun.setStdout("");
            }
        }

        // save the test run into the db
        testRun = testRunRepository.save(testRun);

        return testRun;
    }
}
