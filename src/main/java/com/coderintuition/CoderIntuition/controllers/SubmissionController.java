package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.dtos.*;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.TestCase;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.SubmissionRepository;
import com.coderintuition.CoderIntuition.repositories.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SubmissionController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestRunRepository testRunRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    private ExecutorService scheduler = Executors.newFixedThreadPool(5);

    // wrap the code with the test harness
    private String wrapCode(Problem problem, String userCode, String language, List<TestCase> testCases) {
        if (language.equalsIgnoreCase("python")) {
            String functionName = Utils.getFunctionName(problem.getDefaultCode());
            List<String> codeLines = new ArrayList<String>(Arrays.asList(
                    userCode,
                    "",
                    "",
                    "def test_harness(outputs, test_num, user_input, expected_output):",
                    "    result = " + functionName + "(user_input)",
                    "    if result == expected_output:",
                    "        outputs.append(\"{}|passed\".format(test_num))",
                    "    else:",
                    "        outputs.append(\"{}|failed|{}\".format(test_num, result))",
                    "",
                    "",
                    "outputs = []"
            ));
            // add code for each test case
            for (TestCase testCase : testCases) {
                String input = Utils.formatParam(testCase.getInput(), language);
                String output = Utils.formatParam(testCase.getOutput(), language);
                int num = testCase.getTestCaseNum();
                codeLines.add("input" + num + " = " + input);
                codeLines.add("output" + num + " = " + output);
                codeLines.add("test_harness(outputs, " + num + ", input" + num + ", output" + num + ")");
                codeLines.add("");
            }
            codeLines.add("print(\"----------\")");
            codeLines.add("print(\"\\n\".join(outputs))");

            return String.join("\n", codeLines);
        }
        return "";
    }

    @PostMapping("/submission")
    public SubmissionResponseDto createSubmission(@RequestBody TestRunRequestDto testRunRequestDto) {
        // retrieve the problem
        Problem problem = problemRepository.findById(testRunRequestDto.getProblemId()).orElseThrow();
        // warp the code with the test harness
        String code = wrapCode(problem, testRunRequestDto.getCode(), testRunRequestDto.getLanguage(), problem.getTestCases());

        // create request to JudgeZero
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(Utils.getLanguageId(testRunRequestDto.getLanguage()));
        requestDto.setStdin("");
        JzSubmissionCheckResponseDto result = Utils.callJudgeZero(requestDto, scheduler);

        // create the submission to be saved into the db
        Submission submission = new Submission();
        submission.setCode(testRunRequestDto.getCode());
        submission.setLanguage(testRunRequestDto.getLanguage());
        submission.setProblem(problem);
        submission.setToken(result.getToken());

        // create the submission response dto to be sent back through the api
        SubmissionResponseDto response = new SubmissionResponseDto();
        // error
        if (result.getStatus().getId() >= 6) { // error
            response.setStatus("error");
            submission.setStatus("error");
            String[] error = result.getStderr().split("\n");
            submission.setOutput(error[error.length - 1]);
            response.setStderr(error[error.length - 1]);
        } else if (result.getStatus().getId() == 3) { // no errors
            // everything above the line is stdout, everything below is test results
            String[] split = result.getStdout().trim().split("----------\n");
            submission.setOutput(split[1]);
            // set status as passed at first and overwrite if any test failed
            submission.setStatus("passed");
            response.setStatus("passed");
            List<TestResult> testResults = new ArrayList<>();
            for (String str : split[1].split("\n")) {
                // test results are formatted: {test num}|{status}|{run output}
                String[] testResult = str.split("\\|");
                String num = testResult[0];
                String status = testResult[1];

                // create the test result object to be saved into the db
                TestResult testResultObj = new TestResult();
                testResultObj.setStatus(status);
                // retrieve the test case for this test result
                TestCase testCase = problem.getTestCases().get(Integer.parseInt(num) - 1);
                testResultObj.setInput(testCase.getInput());
                testResultObj.setExpectedOutput(testCase.getOutput());
                testResultObj.setOutput(testCase.getOutput());
                if (status.equals("failed")) {
                    testResultObj.setOutput(testResult[2]);
                    response.setStatus("failed");
                    submission.setStatus("failed");
                }
                // add the test result to the list of test results
                testResults.add(testResultObj);
            }
            response.setTestResults(testResults);
        }
        // save the submission into the db
        submissionRepository.save(submission);

        return response;
    }
}
