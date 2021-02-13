package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.enums.SubmissionStatus;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.TestResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubmissionResponse {
    @NotNull
    private Long id;

    @NotBlank
    private String token;

    @NotNull
    private Language language;

    @NotNull
    private SubmissionStatus status;

    private String output;

    private String stderr;

    private List<TestResult> testResults;

    public static SubmissionResponse fromSubmission(Submission submission) {
        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setId(submission.getId());
        submissionResponse.setToken(submission.getToken());
        submissionResponse.setStatus(submissionResponse.getStatus());
        submissionResponse.setOutput(submissionResponse.getOutput());
        submissionResponse.setStderr(submissionResponse.getStderr());
        submissionResponse.setTestResults(submissionResponse.getTestResults());
        return submissionResponse;
    }
}
