package com.coderintuition.CoderIntuition.dtos.request.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestCaseDto {
    private String name;
    private Boolean isDefault;
    private String input;
    private String output;
}
