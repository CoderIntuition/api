package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.dtos.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.dtos.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.dtos.TestRunRequestDto;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Solution;
import com.coderintuition.CoderIntuition.models.TestRun;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.SubmissionRepository;
import com.coderintuition.CoderIntuition.repositories.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
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

    private ExecutorService scheduler = Executors.newFixedThreadPool(5);

    // wrap the code with the test harness and the solution code
    private String wrapCode(String userCode, String solution, String language, String input) {
        if (language.equalsIgnoreCase("python")) {
            String functionName = Utils.getFunctionName(userCode);
            // TODO: support multiple params
            String param = Utils.formatParam(input, language);
            List<String> codeLines = Arrays.asList(
                    userCode,
                    "",
                    "",
                    // append _sol to the end of the solution function
                    solution.replace(functionName, functionName + "_sol"),
                    "",
                    "",
                    "user_result = " + functionName + "(" + param + ")",
                    "sol_result = " + functionName + "_sol(" + param + ")",
                    "print(\"----------\")",
                    "if user_result == sol_result:",
                    "    print(\"passed|{}|{}\".format(sol_result, user_result))",
                    "else:",
                    "    print(\"failed|{}|{}\".format(sol_result, user_result))"
            );
            return String.join("\n", codeLines);
        }
        return "";
    }

    @PostMapping("/testrun")
    public TestRun createTestRun(@RequestBody TestRunRequestDto testRunRequestDto) {
        // retrieve the problem
        Problem problem = problemRepository.findById(testRunRequestDto.getProblemId()).orElseThrow();
        // retrieve the primary solution
        Solution primarySolution = problem.getSolutions().stream().filter(Solution::getIsPrimary).findFirst().orElseThrow();
        // warp the code with the test harness
        String code = wrapCode(testRunRequestDto.getCode(), primarySolution.getCode(),
                testRunRequestDto.getLanguage(), testRunRequestDto.getInput());

        // create request to JudgeZero
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(Utils.getLanguageId(testRunRequestDto.getLanguage()));
        requestDto.setStdin("");
        // send request to JudgeZero
        JzSubmissionCheckResponseDto result = Utils.callJudgeZero(requestDto, scheduler);

        // create the test run to be saved into the db
        TestRun testRun = new TestRun();
        testRun.setProblem(problem);
        testRun.setToken(result.getToken());
        testRun.setLanguage(testRunRequestDto.getLanguage());
        testRun.setCode(testRunRequestDto.getCode());
        testRun.setInput(testRunRequestDto.getInput());

        // save the results of the test run
        if (result.getStatus().getId() >= 6) { // error
            testRun.setStatus("error");
            testRun.setExpectedOutput("");
            testRun.setOutput("");
            String[] error = result.getStderr().split("\n");
            testRun.setStderr(error[error.length - 1]);
            testRun.setStdout("");
        } else if (result.getStatus().getId() == 3) { // no errors
            // everything above the line is stdout, everything below is test results
            String[] split = result.getStdout().trim().split("----------\n");
            testRun.setStdout(split[0]);
            // test results are formatted: {status}|{expected output}|{run output}
            String[] testResult = split[1].split("\\|");
            testRun.setStatus(testResult[0]);
            testRun.setExpectedOutput(testResult[1]);
            testRun.setOutput(testResult[2]);
            testRun.setStderr("");
        }
        // save the test run into the db
        testRun = testRunRepository.save(testRun);

        return testRun;
    }
}
