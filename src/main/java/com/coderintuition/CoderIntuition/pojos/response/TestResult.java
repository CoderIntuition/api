package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.TestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestResult {
    private TestStatus status;
    private String input;
    private String expectedOutput;
    private String output;
}
