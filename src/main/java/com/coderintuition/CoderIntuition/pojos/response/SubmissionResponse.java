package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.enums.SubmissionStatus;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.TestResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
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

    @JsonIgnoreProperties("submission")
    private List<TestResult> testResults;

    private Date created_at;

    public static SubmissionResponse fromSubmission(Submission submission) {
        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setId(submission.getId());
        submissionResponse.setToken(submission.getToken());
        submissionResponse.setLanguage(submission.getLanguage());
        submissionResponse.setStatus(submission.getStatus());
        submissionResponse.setOutput(submission.getOutput());
        submissionResponse.setStderr(submission.getStderr());
        submissionResponse.setTestResults(submission.getTestResults());
        submissionResponse.setCreated_at(submission.getCreated_at());
        return submissionResponse;
    }
}
