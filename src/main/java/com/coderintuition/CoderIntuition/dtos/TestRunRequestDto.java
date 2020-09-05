package com.coderintuition.CoderIntuition.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestRunRequestDto {

    private Long problemId;
    private String language;
    private String input;
    private String code;
}
