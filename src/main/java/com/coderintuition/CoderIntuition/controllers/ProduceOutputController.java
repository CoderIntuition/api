package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.pojos.request.ProduceOutputDto;
import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.pojos.response.ProduceOutputResponse;
import com.coderintuition.CoderIntuition.models.Language;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ProduceOutputController {

    private ExecutorService scheduler = Executors.newFixedThreadPool(5);

    // wrap the code with the test harness and the solution code
    private String wrapCode(String code, String input) {
        String functionName = Utils.getFunctionName(Language.PYTHON, code);
        // TODO: support multiple params
        String param = Utils.formatParam(input, Language.PYTHON);
        // TODO: put this into a text file with args
        List<String> codeLines = Arrays.asList(
                code,
                "",
                "",
                "result = " + functionName + "(" + param + ")",
                "print(\"----------\")",
                "print(\"SUCCESS|{}\".format(result))"
        );
        return String.join("\n", codeLines);
    }

    @PostMapping("/produceoutput")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ProduceOutputResponse produceOutput(@RequestBody ProduceOutputDto produceOutputDto) {
        // warp the code with the test harness
        String code = wrapCode(produceOutputDto.getCode(), produceOutputDto.getInput());

        // TODO: support different languages

        // create request to JudgeZero
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(Utils.getLanguageId(Language.PYTHON));
        requestDto.setStdin("");
        // send request to JudgeZero
        JzSubmissionCheckResponseDto result = Utils.callJudgeZero(requestDto, scheduler);

        // set the results
        ProduceOutputResponse response = new ProduceOutputResponse();
        if (result.getStatus().getId() >= 6) { // error
            response.setStatus("ERROR");
            response.setOutput("");
            String[] error = result.getStderr().split("\n");
            response.setStderr(error[error.length - 1]);
            response.setStdout("");
        } else if (result.getStatus().getId() == 3) { // no errors
            // everything above the line is stdout, everything below is results
            String[] split = result.getStdout().trim().split("----------\n");
            response.setStdout(split[0]);
            // results are formatted: {status}|{output}
            String[] testResult = split[1].split("\\|");
            response.setStatus(testResult[0]);
            response.setOutput(testResult[1]);
            response.setStderr("");
        }

        return response;
    }
}
