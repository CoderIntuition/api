package com.coderintuition.CoderIntuition.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestRunResponseDto {

    private String stdout;
    private String stderr;
    private String output;
    private String expectedOutput;
    private String status;
}
