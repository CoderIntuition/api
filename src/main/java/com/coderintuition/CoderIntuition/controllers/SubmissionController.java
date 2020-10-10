package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.dtos.*;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.Testcase;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.SubmissionRepository;
import com.coderintuition.CoderIntuition.repositories.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.*;

@RestController
public class SubmissionController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestRunRepository testRunRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    private ExecutorService scheduler = Executors.newFixedThreadPool(5);

    private String wrapCode2(Problem problem, String userCode, String language, List<Testcase> testcases) {
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
            for (Testcase testcase : testcases) {
                String input = Utils.formatParam(testcase.getInput(), language);
                String output = Utils.formatParam(testcase.getOutput(), language);
                int num = testcase.getTestcaseNum();
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
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
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
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        try {
            try {
                future.get(20, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                ex.printStackTrace();
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        return responseData[0];
    }

    @PostMapping("/submission")
    public SubmissionResponseDto createSubmission(@RequestBody TestRunRequestDto testRunRequestDto) {
        Problem problem = problemRepository.findById(testRunRequestDto.getProblemId()).orElseThrow();
        String code = wrapCode2(problem, testRunRequestDto.getCode(), testRunRequestDto.getLanguage(), problem.getTestcases());

        JZSubmissionRequestDto requestDto = new JZSubmissionRequestDto();
        requestDto.setSourceCode(code);
        requestDto.setLanguageId(Utils.getLanguageId(testRunRequestDto.getLanguage()));
        requestDto.setStdin("");
        JzSubmissionCheckResponseDto result = callJudgeZero(requestDto);

        Submission submission = new Submission();
        submission.setCode(testRunRequestDto.getCode());
        submission.setLanguage(testRunRequestDto.getLanguage());
        submission.setProblem(problem);
        submission.setToken(result.getToken());

        SubmissionResponseDto response = new SubmissionResponseDto();
        // error
        if (result.getStatus().getId() >= 6) { // complication error
            response.setStatus("error");
            submission.setStatus("error");
            String[] error = result.getStderr().split("\n");
            submission.setOutput(error[error.length - 1]);
            response.setStderr(error[error.length - 1]);
        } else if (result.getStatus().getId() == 3) { // no compilation error
            String[] split = result.getStdout().trim().split("----------\n");
            submission.setOutput(split[1]);
            submission.setStatus("passed");
            response.setStatus("passed");
            List<TestResult> testResults = new ArrayList<>();
            for (String str : split[1].split("\n")) {
                String num = str.split("\\|")[0];
                String status = str.split("\\|")[1];
                TestResult testResult = new TestResult();
                testResult.setStatus(status);
                Testcase testcase = problem.getTestcases().get(Integer.parseInt(num) - 1);
                testResult.setInput(testcase.getInput());
                testResult.setExpectedOutput(testcase.getOutput());
                testResult.setOutput(testcase.getOutput());
                if (status.equals("failed")) {
                    testResult.setOutput(str.split("\\|")[2]);
                    response.setStatus("failed");
                    submission.setStatus("failed");
                }
                testResults.add(testResult);
            }
            response.setTestResults(testResults);
        }
        submission = submissionRepository.save(submission);

        return response;
    }
}
