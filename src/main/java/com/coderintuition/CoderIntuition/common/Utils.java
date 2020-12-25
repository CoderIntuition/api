package com.coderintuition.CoderIntuition.common;

import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.pojos.response.JZSubmissionResponseDto;
import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.models.Language;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;

public class Utils {
    public static int getLanguageId(Language language) {
        if (language == Language.PYTHON) {
            return 71;
        } else if (language == Language.JAVA) {
            return 62;
        } else if (language == Language.JAVASCRIPT) {
            return 63;
        }
        return -1;
    }

    public static String getFunctionName(Language language, String defaultCode) {
        switch (language) {
            case PYTHON:
                int startIndex = defaultCode.indexOf("def ") + 4;
                int endIndex = defaultCode.indexOf("(", startIndex);
                return defaultCode.substring(startIndex, endIndex);
            case JAVA:
                endIndex = defaultCode.indexOf("(");
                int cur = endIndex - 1;
                while (defaultCode.charAt(cur) != ' ') {
                    cur--;
                }
                return defaultCode.substring(cur + 1, endIndex);
            default:
                return "test";
        }
    }

    public static String formatParam(String param, Language language) {
        if (language == Language.JAVA) {

        } else if (language == Language.PYTHON) {

        }
        return param;
    }

    public static String formatErrorMessage(Language language, String err) {
        switch (language) {
            case PYTHON:
                return err.replaceAll("File .* line \\d+ *\\n", "");
            case JAVA:
                return err.replaceAll("Main\\.java:\\d+: ", "");
            default:
                return err;
        }
    }

    public static JzSubmissionCheckResponseDto callJudgeZero(JZSubmissionRequestDto requestDto, ExecutorService scheduler) {
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

    public static String generateUsername() {
        String aToZ = "abcdefghjkmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randIndex = rand.nextInt(aToZ.length());
            res.append(aToZ.charAt(randIndex));
        }
        return res.toString();
    }
}
