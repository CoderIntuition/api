package com.coderintuition.CoderIntuition.dtos.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunRequestDto {

    private Long problemId;
    private String language;
    private String input;
    private String code;
}
