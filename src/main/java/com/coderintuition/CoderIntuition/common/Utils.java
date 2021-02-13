package com.coderintuition.CoderIntuition.common;

import com.coderintuition.CoderIntuition.config.AppProperties;
import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
import com.coderintuition.CoderIntuition.pojos.response.JZSubmissionResponse;
import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.Response;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

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
            case JAVASCRIPT:
                startIndex = defaultCode.indexOf("function ") + 9;
                endIndex = defaultCode.indexOf("(", startIndex);
                return defaultCode.substring(startIndex, endIndex);
            default:
                return "";
        }
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

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String submitToJudgeZero(JZSubmissionRequestDto requestDto, AppProperties appProperties) throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();
        requestDto.setSourceCode(encoder.encodeToString(requestDto.getSourceCode().getBytes()));
        requestDto.setStdin(encoder.encodeToString(requestDto.getStdin().getBytes()));

        ObjectMapper mapper = new ObjectMapper();

        OkHttpClient client = new OkHttpClient();
        String url = appProperties.getJudge0().getUrl() + "/submissions?base64_encoded=true";
        RequestBody body = RequestBody.create(mapper.writeValueAsString(requestDto), JSON);

        Request request = new Request.Builder()
            .url(url)
            .addHeader("content-type", "application/json")
            .addHeader("x-rapidapi-host", appProperties.getJudge0().getRapidApiHost())
            .addHeader("x-rapidapi-key", appProperties.getJudge0().getRapidApiKey())
            .post(body)
            .build();

        Call call = client.newCall(request);
        okhttp3.Response response = call.execute();
        JZSubmissionResponse responseDto = mapper.readValue(Objects.requireNonNull(response.body()).string(),
            JZSubmissionResponse.class);

        return responseDto.getToken();
    }

    public static JzSubmissionCheckResponse retrieveFromJudgeZero(String token, AppProperties appProperties) throws IOException {
        // get test run info
        OkHttpClient client = new OkHttpClient();
        String url = appProperties.getJudge0().getUrl() + "/submissions/" + token + "?base64_encoded=true";

        Request request = new Request.Builder()
            .url(url)
            .addHeader("x-rapidapi-host", appProperties.getJudge0().getRapidApiHost())
            .addHeader("x-rapidapi-key", appProperties.getJudge0().getRapidApiKey())
            .get()
            .build();

        okhttp3.Response response = client.newCall(request).execute();
        ObjectMapper mapper = new ObjectMapper();

        Base64.Decoder decoder = Base64.getMimeDecoder();

        JzSubmissionCheckResponse jzSubmissionCheckResponse = mapper.readValue(Objects.requireNonNull(response.body()).string(), JzSubmissionCheckResponse.class);
        if (jzSubmissionCheckResponse.getStderr() != null) {
            byte[] stderrBytes = decoder.decode(jzSubmissionCheckResponse.getStderr());
            jzSubmissionCheckResponse.setStderr(new String(stderrBytes));
        }
        if (jzSubmissionCheckResponse.getStdout() != null) {
            byte[] stdoutBytes = decoder.decode(jzSubmissionCheckResponse.getStdout());
            jzSubmissionCheckResponse.setStdout(new String(stdoutBytes));
        }
        if (jzSubmissionCheckResponse.getCompileOutput() != null) {
            byte[] compileOutputBytes = decoder.decode(jzSubmissionCheckResponse.getCompileOutput());
            jzSubmissionCheckResponse.setCompileOutput(new String(compileOutputBytes));
        }
        if (jzSubmissionCheckResponse.getMessage() != null) {
            byte[] messageBytes = decoder.decode(jzSubmissionCheckResponse.getMessage());
            jzSubmissionCheckResponse.setMessage(new String(messageBytes));
        }

        return jzSubmissionCheckResponse;
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

    public static String fileToString(String fileName) {
        try {
            Resource resource = new ClassPathResource(fileName);
            InputStream inputStream = resource.getInputStream();
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error";
        }
    }

    public static void sendEmail(String key, String to, String subject, String html) {
        Configuration configuration = new Configuration()
            .domain("coderintuition.com")
            .apiKey(key)
            .from("CoderIntuition", "support@coderintuition.com");
        Response response = Mail.using(configuration)
            .to(to)
            .subject(subject)
            .html(html)
            .build()
            .send();

        if (!response.isOk()) {
            System.out.println("Error sending email to " + to + ". Response code: "
                + response.responseCode() + " with message: " + response.responseMessage());
        }
    }
}
