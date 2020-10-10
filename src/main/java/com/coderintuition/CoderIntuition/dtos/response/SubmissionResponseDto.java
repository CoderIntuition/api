package com.coderintuition.CoderIntuition.dtos.response;

import com.coderintuition.CoderIntuition.models.TestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubmissionResponseDto {
    private TestStatus status;
    private String stderr;
    private List<TestResult> testResults;
}
