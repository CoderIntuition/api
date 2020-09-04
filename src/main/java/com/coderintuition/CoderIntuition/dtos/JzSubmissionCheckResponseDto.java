package com.coderintuition.CoderIntuition.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JzSubmissionCheckResponseDto {
    @JsonProperty("compile_output")
    private String compileOutput;
    private int memory;
    private String message;
    private Status status;
    private String stderr;
    private String stdout;
    private String time;
    private String token;
}
