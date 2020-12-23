package com.coderintuition.CoderIntuition.pojos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestResult {
    private String status;
    private String input;
    private String expectedOutput;
    private String output;
}
