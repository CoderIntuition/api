package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.CodeTemplateFiller;
import com.coderintuition.CoderIntuition.common.Constants;
import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.pojos.request.ProduceOutputDto;
import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.pojos.response.ProduceOutputResponse;
import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ProduceOutputController {
    @Autowired
    ProblemRepository problemRepository;

    private final ExecutorService scheduler = Executors.newFixedThreadPool(5);

    @PostMapping("/produceoutput")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ProduceOutputResponse produceOutput(@RequestBody ProduceOutputDto produceOutputDto) {
        // retrieve the problem
        Problem problem = problemRepository.findById(produceOutputDto.getProblemId()).orElseThrow();

        // wrap the code into the test run template
        CodeTemplateFiller filler = CodeTemplateFiller.getInstance();
        String functionName = Utils.getFunctionName(produceOutputDto.getLanguage(), problem.getCode(produceOutputDto.getLanguage()));
        String code = filler.getProduceOutputCode(produceOutputDto.getLanguage(), produceOutputDto.getCode(),
                functionName, problem.getArguments(), problem.getReturnType());

        // create request to JudgeZero
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(Utils.getLanguageId(Language.PYTHON));
        requestDto.setStdin(produceOutputDto.getInput());
        // send request to JudgeZero
        JzSubmissionCheckResponseDto result = Utils.callJudgeZero(requestDto, scheduler);

        // set the results
        ProduceOutputResponse response = new ProduceOutputResponse();
        if (result.getStatus().getId() >= 6) { // error
            response.setStatus("ERROR");
            String stderr = "";
            if (result.getCompileOutput() != null) {
                stderr = result.getCompileOutput();
            } else if (result.getStderr() != null) {
                stderr = result.getStderr();
            }
            response.setStderr(Utils.formatErrorMessage(produceOutputDto.getLanguage(), stderr));
            response.setStdout("");

        } else if (result.getStatus().getId() == 3) { // no compile errors
            // everything above the line is stdout, everything below is test results
            String[] split = result.getStdout().trim().split(Constants.IO_SEPARATOR);
            String[] testResult = split[1].split("\\|");

            if (testResult[0].equals("SUCCESS")) { // no errors
                // test results are formatted: {status}|{run output}
                response.setStatus("SUCCESS");
                response.setOutput(testResult[1]);
                response.setStdout(split[0]);
                response.setStderr("");

            } else if (testResult[0].equals("ERROR")) { // runtime errors
                // runtime error results are formatted: {status}|{error message}
                response.setStatus("ERROR");
                response.setOutput("");
                response.setStderr(Utils.formatErrorMessage(produceOutputDto.getLanguage(), testResult[1]));
                response.setStdout("");
            }
        }

        return response;
    }
}
