package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.enums.SubmissionStatus;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.TestResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
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
    @Expose
    private Long problemId;

    @NotNull
    @Expose
    private Language language;

    @NotNull
    @Expose
    private SubmissionStatus status;

    @NotNull
    @Expose
    private String code;

    @Expose
    private String output;

    @Expose
    private String stderr;

    @JsonIgnoreProperties("submission")
    private List<TestResult> testResults;

    @Expose
    private Date created_at;

    @Override
    public String toString() {
        return Utils.excludeGson.toJson(this);
    }

    public static SubmissionResponse fromSubmission(Submission submission) {
        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setProblemId(submission.getProblem().getId());
        submissionResponse.setLanguage(submission.getLanguage());
        submissionResponse.setStatus(submission.getStatus());
        submissionResponse.setCode(submission.getCode());
        submissionResponse.setOutput(submission.getOutput());
        submissionResponse.setStderr(submission.getStderr());
        submissionResponse.setTestResults(submission.getTestResults());
        submissionResponse.setCreated_at(submission.getCreated_at());
        return submissionResponse;
    }
}
