package com.coderintuition.CoderIntuition.util;

import com.coderintuition.CoderIntuition.dtos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.dtos.response.JZSubmissionResponseDto;
import com.coderintuition.CoderIntuition.dtos.response.JzSubmissionCheckResponseDto;
import com.coderintuition.CoderIntuition.models.Language;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    public static String getFunctionName(String defaultCode) {
        return defaultCode.substring(4, defaultCode.indexOf("("));
    }

    public static String formatParam(String param, Language language) {
        if (language == Language.JAVA) {

        } else if (language == Language.PYTHON) {

        }
        return param;
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
}
