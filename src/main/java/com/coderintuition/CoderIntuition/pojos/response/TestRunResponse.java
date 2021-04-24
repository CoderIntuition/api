package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.TestStatus;
import com.coderintuition.CoderIntuition.models.TestRun;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class TestRunResponse {
    @NotBlank
    private Long problemId;

    @NotBlank
    private String input;

    @NotNull
    private TestStatus status;

    private String expectedOutput;

    private String output;

    private String stdout;

    private String stderr;

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }

    public static TestRunResponse fromTestRun(TestRun testRun) {
        TestRunResponse testRunResponse = new TestRunResponse();
        testRunResponse.setProblemId(testRun.getProblem().getId());
        testRunResponse.setInput(testRun.getInput());
        testRunResponse.setStatus(testRun.getStatus());
        testRunResponse.setExpectedOutput(testRun.getExpectedOutput());
        testRunResponse.setOutput(testRun.getOutput());
        testRunResponse.setStdout(testRun.getStdout());
        testRunResponse.setStderr(testRun.getStderr());
        return testRunResponse;
    }
}
