package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.dtos.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.dtos.JZSubmissionResponseDto;
import com.coderintuition.CoderIntuition.dtos.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.dtos.TestRunRequestDto;
import com.coderintuition.CoderIntuition.models.*;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.SubmissionRepository;
import com.coderintuition.CoderIntuition.repositories.SubmissionRunRepository;
import com.coderintuition.CoderIntuition.repositories.TestRunRepository;
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
public class TestRunController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestRunRepository testRunRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    SubmissionRunRepository submissionRunRepository;

    private ExecutorService scheduler = Executors.newFixedThreadPool(5);

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
            return String.join("\n", codeLines);
        }
        return "";
    }

    private String wrapCode2(Problem problem, String userCode, String solutionCode, String language, String input) {
        if (language.equalsIgnoreCase("python")) {
            String functionName = getFunctionName(problem.getDefaultCode());
            List<String> paramList = getFunctionParams(input, language);
            String paramStr = String.join(", ", paramList);
            List<String> codeLines = Arrays.asList(
                    userCode,
                    "",
                    "",
                    solutionCode.replace(functionName, functionName + "_sol"),
                    "",
                    "",
                    "user_result = " + functionName + "(" + paramStr + ")",
                    "solution_result = " + functionName + "_sol" + "(" + paramStr + ")",
                    "print(\"----------------------\")",
                    "print(user_result)",
                    "print(\"----------------------\")",
                    "print(solution_result)");
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
                    if (statusId >= 3) {
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

    private JzSubmissionCheckResponseDto sendUserTestRun(TestRunRequestDto testRunRequestDto, Problem problem) {
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        String code = wrapCode(problem, testRunRequestDto.getCode(),
                testRunRequestDto.getLanguage(), testRunRequestDto.getInput());
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(getLanguageId(testRunRequestDto.getLanguage()));
        requestDto.setStdin("");

        return callJudgeZero(requestDto);
    }

    private JzSubmissionCheckResponseDto sendSolutionTestRun(TestRunRequestDto testRunRequestDto, Problem problem) {
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        Solution primarySolution = problem.getSolutions().stream().filter(Solution::getIsPrimary).findFirst().orElseThrow();

        String code = wrapCode(problem, primarySolution.getCode(), testRunRequestDto.getLanguage(), testRunRequestDto.getInput());
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(getLanguageId(testRunRequestDto.getLanguage()));
        requestDto.setStdin("");

        return callJudgeZero(requestDto);
    }

    @PostMapping("/testrun")
    public TestRun createTestRun(@RequestBody TestRunRequestDto testRunRequestDto) {

        Problem problem = problemRepository.findById(testRunRequestDto.getProblemId()).orElseThrow();
        JzSubmissionCheckResponseDto userResult = sendUserTestRun(testRunRequestDto, problem);
        JzSubmissionCheckResponseDto solutionResult = sendSolutionTestRun(testRunRequestDto, problem);
        TestRun testRun = new TestRun();
        testRun.setProblem(problem);
        testRun.setLanguage(testRunRequestDto.getLanguage());
        testRun.setCode(testRunRequestDto.getCode());

        // error
        if (userResult.getStatus().getId() >= 6) {
            testRun.setStatus("error");
            testRun.setExpectedOutput("");
            testRun.setOutput("");
            testRun.setStderr(userResult.getStderr());
            testRun.setStdout("");
            // run success, check if output matches expected output
        } else if (userResult.getStatus().getId() == 3) {
            String userStdout = userResult.getStdout().split("--- OUTPUT START ---\n")[0];
            String userOutput = userResult.getStdout().split("--- OUTPUT START ---\n")[1].trim();
            String solutionOutput = solutionResult.getStdout().split("--- OUTPUT START ---\n")[1].trim();

            testRun.setStdout(userStdout);
            testRun.setOutput(userOutput);
            testRun.setExpectedOutput(solutionOutput);
            testRun.setStatus(userOutput.equals(solutionOutput) ? "passed" : "failed");
            testRun.setStderr("");
        }

        testRun = testRunRepository.save(testRun);
        return testRun;
    }

    @PostMapping("/submission")
    public Submission createSubmission(@RequestBody TestRunRequestDto testRunRequestDto) {
        Problem problem = problemRepository.findById(testRunRequestDto.getProblemId()).orElseThrow();
        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        Solution primarySolution = problem.getSolutions().stream().filter(Solution::getIsPrimary).findFirst().orElseThrow();

        Submission submission = new Submission();
        submission.setCode(testRunRequestDto.getCode());
        submission.setLanguage(testRunRequestDto.getLanguage());
        submission.setProblem(problem);
        List<SubmissionRun> submissionRuns = new ArrayList<>();
        boolean passed = true;

        for (Testcase testcase : problem.getTestcases()) {
            String code = wrapCode2(problem, testRunRequestDto.getCode(), primarySolution.getCode(), testRunRequestDto.getLanguage(), testcase.getInput());
            requestDto.setSourceCode(code);
            requestDto.setLanguageId(getLanguageId(testRunRequestDto.getLanguage()));
            requestDto.setStdin("");
            JzSubmissionCheckResponseDto result = callJudgeZero(requestDto);

            SubmissionRun submissionRun = new SubmissionRun();
            submissionRun.setToken(result.getToken());

            // error
            if (result.getStatus().getId() >= 6) {
                submissionRun.setStatus("error");
                submissionRun.setExpectedOutput("");
                submissionRun.setOutput("");
                submissionRun.setStderr(result.getStderr());
                passed = false;
                // run success, check if output matches expected output
            } else if (result.getStatus().getId() == 3) {
                String userOutput = result.getStdout().split("----------------------\n")[1].trim();
                String solutionOutput = result.getStdout().split("----------------------\n")[2].trim();

                submissionRun.setOutput(userOutput);
                submissionRun.setExpectedOutput(solutionOutput);
                if (userOutput.equals(solutionOutput)) {
                    submissionRun.setStatus("passed");
                } else {
                    submissionRun.setStatus("failed");
                    passed = false;
                }
                submissionRun.setStderr("");
            }

            submissionRun = submissionRunRepository.save(submissionRun);
            submissionRuns.add(submissionRun);
        }

        submission.setStatus(passed ? "passed" : "failed");
        submission.setSubmissionRuns(submissionRuns);
        submission = submissionRepository.save(submission);

        return submission;
    }
}
