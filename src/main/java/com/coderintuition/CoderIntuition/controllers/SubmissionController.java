package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.dtos.*;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Solution;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
public class SubmissionController {

    @Autowired
    ProblemRepository problemRepository;

    ExecutorService scheduler = Executors.newFixedThreadPool(5);

    private int getLanguageId(String language) {
        if (language.equalsIgnoreCase("python")) {
            return 71;
        } else if (language.equalsIgnoreCase("java")) {
            return 62;
        } else if (language.equalsIgnoreCase("javascript")) {
            return 63;
        }
        return -1;
    }

    private String getFunctionName(String defaultCode) {
        return defaultCode.substring(4, defaultCode.indexOf("("));
    }

    private List<String> getFunctionParams(String input, String language) {
        List<String> paramList = Arrays.asList(input.split("\n"));
        if (language.equalsIgnoreCase("java")) {
            paramList = paramList.stream().map(param -> {
                if (param.startsWith("[") && param.endsWith("]")) {
                    return "Arrays.asList(" + param.substring(1, param.length() - 1) + ")";
                }
                return param;
            }).collect(Collectors.toList());
        }
        return paramList;
    }

    private String wrapCode(Problem problem, String code, String language, String input) {
        if (language.equalsIgnoreCase("python")) {
            String functionName = getFunctionName(problem.getDefaultCode());
            List<String> paramList = getFunctionParams(input, language);
            String paramStr = String.join(", ", paramList);
            List<String> codeLines = Arrays.asList(
                    code,
                    "",
                    "",
                    "result = " + functionName + "(" + paramStr + ")",
                    "print(\"--- OUTPUT START ---\")",
                    "print(result)");
            System.out.println("------ START CODE ------");
            System.out.println(String.join("\n", codeLines));
            System.out.println("------ END CODE ------");
            return String.join("\n", codeLines);
        }
        return "";
    }

    private JzSubmissionCheckResponseDto callJudgeZero(JZSubmissionRequestDto requestDto) {
        Map<String, String> header = new HashMap<>();
        header.put("content-type", "application/json");
        header.put("x-rapidapi-host", "judge0.p.rapidapi.com");
        header.put("x-rapidapi-key", "570c3ea12amsh7d718c55ca5d164p153fd5jsnfca4d3b2f9f9");

        Mono<JZSubmissionResponseDto> response = WebClient
                .create("https://judge0.p.rapidapi.com")
                .post()
                .uri("/submissions")
                .headers(httpHeaders -> httpHeaders.setAll(header))
                .body(Mono.just(requestDto), JZSubmissionRequestDto.class)
                .retrieve()
                .bodyToMono(JZSubmissionResponseDto.class);
        String token = Objects.requireNonNull(response.block()).getToken();

        final JzSubmissionCheckResponseDto[] responseData = new JzSubmissionCheckResponseDto[1];
        Future<?> future = scheduler.submit(() -> {
            try {
                while (true) {
                    Map<String, String> header1 = new HashMap<>();
                    header1.put("x-rapidapi-host", "judge0.p.rapidapi.com");
                    header1.put("x-rapidapi-key", "570c3ea12amsh7d718c55ca5d164p153fd5jsnfca4d3b2f9f9");

                    Mono<JzSubmissionCheckResponseDto> response1 = WebClient
                            .create("https://judge0.p.rapidapi.com")
                            .get()
                            .uri("/submissions/{token}", token)
                            .headers(httpHeaders -> httpHeaders.setAll(header1))
                            .retrieve()
                            .bodyToMono(JzSubmissionCheckResponseDto.class);

                    responseData[0] = Objects.requireNonNull(response1.block());
                    int statusId = responseData[0].getStatus().getId();
                    if (statusId != 1 && statusId != 2) {
                        System.out.println("STATUS: " + responseData[0].getStatus().getDescription());
                        break;
                    }
                    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        try {
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                ex.printStackTrace();
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        return responseData[0];
    }

    private JzSubmissionCheckResponseDto executeUserSubmission(SubmissionRequestDto submissionRequestDto) {
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        Problem problem = problemRepository.findById(submissionRequestDto.getProblemId()).orElseThrow();
        String code = wrapCode(problem, submissionRequestDto.getCode(),
                submissionRequestDto.getLanguage(), submissionRequestDto.getInput());
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(getLanguageId(submissionRequestDto.getLanguage()));
        requestDto.setStdin("");

        return callJudgeZero(requestDto);
    }

    private JzSubmissionCheckResponseDto executeSolutionSubmission(SubmissionRequestDto submissionRequestDto) {
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        Problem problem = problemRepository.findById(submissionRequestDto.getProblemId()).orElseThrow();
        Solution primarySolution = problem.getSolutions().stream().filter(Solution::getIsPrimary).findFirst().orElseThrow();

        String code = wrapCode(problem, primarySolution.getCode(), submissionRequestDto.getLanguage(), submissionRequestDto.getInput());
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(getLanguageId(submissionRequestDto.getLanguage()));
        requestDto.setStdin("");

        return callJudgeZero(requestDto);
    }

    @PostMapping("/submission")
    public SubmissionResponseDto createSubmission(@RequestBody SubmissionRequestDto submissionRequestDto) {

        SubmissionResponseDto responseDto = new SubmissionResponseDto();

        System.out.println("=============================================");
        JzSubmissionCheckResponseDto userResult = executeUserSubmission(submissionRequestDto);
        JzSubmissionCheckResponseDto solutionResult = executeSolutionSubmission(submissionRequestDto);

        // error
        if (userResult.getStatus().getId() >= 6) {
            responseDto.setStatus("error");
            responseDto.setExpectedOutput("");
            responseDto.setOutput("");
            responseDto.setStderr(userResult.getStderr());
            responseDto.setStdout("");
        } else if (userResult.getStatus().getId() == 3) {
            String userStdout = userResult.getStdout().split("--- OUTPUT START ---\n")[0];
            String userOutput = userResult.getStdout().split("--- OUTPUT START ---\n")[1].trim();
            String solutionOutput = solutionResult.getStdout().split("--- OUTPUT START ---\n")[1].trim();

            responseDto.setStdout(userStdout);
            responseDto.setOutput(userOutput);
            responseDto.setExpectedOutput(solutionOutput);
            responseDto.setStatus(userOutput.equals(solutionOutput) ? "passed" : "failed");
            responseDto.setStderr("");
        }
        System.out.println("STATUS: " + responseDto.getStatus());
        System.out.println("STDOUT: " + responseDto.getStdout());
        System.out.println("STDERR: " + responseDto.getStderr());
        System.out.println("OUTPUT: " + responseDto.getOutput());
        System.out.println("EXPECTED OUTPUT: " + responseDto.getExpectedOutput());
        System.out.println("=============================================");

        return responseDto;
    }
}
