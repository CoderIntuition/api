package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.CodeTemplateFiller;
import com.coderintuition.CoderIntuition.common.Constants;
import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.config.AppProperties;
import com.coderintuition.CoderIntuition.enums.TestStatus;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Solution;
import com.coderintuition.CoderIntuition.models.TestRun;
import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.pojos.request.RunRequestDto;
import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponse;
import com.coderintuition.CoderIntuition.pojos.response.TestRunResponse;
import com.coderintuition.CoderIntuition.pojos.response.TokenResponse;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.TestRunRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
public class TestRunController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestRunRepository testRunRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    AppProperties appProperties;

    @PutMapping("/testrun/judge0callback")
    public void testRunCallback(@RequestBody JzSubmissionCheckResponse data) throws Exception {
        log.info("PUT /testrun/judge0callback");
        log.info("data={}", data.toString());

        // get test run info
        JzSubmissionCheckResponse result = Utils.retrieveFromJudgeZero(data.getToken(), appProperties);
        log.info("result={}", result.toString());

        // update the test run in the db
        TestRun testRun = testRunRepository.findByToken(result.getToken()).orElseThrow();
        log.info("testRun={}", testRun.toString());

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
            testRun.setStderr(Utils.formatErrorMessage(testRun.getLanguage(), stderr));
            testRun.setStdout("");

        } else if (result.getStatus().getId() == 3) { // no compile errors
            // everything above the line is stdout, everything below is test results
            String[] split = result.getStdout().trim().split(Constants.IO_SEPARATOR);
            String[] testResult = split[1].split("\\|");
            log.info("testResult={}", Arrays.toString(testResult));

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
                testRun.setStderr(Utils.formatErrorMessage(testRun.getLanguage(), testResult[1]));
                testRun.setStdout("");
            }
        }

        // save the test run into the db
        testRun = testRunRepository.save(testRun);

        // create TestRunResponse to send back over websocket
        TestRunResponse testRunResponse = TestRunResponse.fromTestRun(testRun);

        // send message to frontend over websocket
        ObjectMapper mapper = new ObjectMapper();
        this.simpMessagingTemplate.convertAndSend("/topic/testrun", mapper.writeValueAsString(testRunResponse));
    }

    @PostMapping("/testrun")
    public TokenResponse createTestRun(@RequestBody RunRequestDto runRequestDto) throws Exception {
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
        requestDto.setCallbackUrl(appProperties.getJudge0().getCallbackUrl() + "/testrun/judge0callback");

        // send request to JudgeZero
        String token = Utils.submitToJudgeZero(requestDto, appProperties);

        // create the test run to be saved into the db
        TestRun testRun = new TestRun();
        testRun.setProblem(problem);
        testRun.setToken(token);
        testRun.setLanguage(runRequestDto.getLanguage());
        testRun.setCode(runRequestDto.getCode());
        testRun.setInput(runRequestDto.getInput());
        testRun.setStatus(TestStatus.RUNNING);
        testRunRepository.save(testRun);

        return new TokenResponse(token);
    }
}
