package com.coderintuition.CoderIntuition.dtos.request.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class TestCaseDto {
    @NotBlank
    @Size(max = 300)
    private String name;

    @NotNull
    private Boolean isDefault;

    @NotBlank
    private String input;

    @NotBlank
    private String output;
}
