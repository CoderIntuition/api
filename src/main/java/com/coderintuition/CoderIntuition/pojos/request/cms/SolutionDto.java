package com.coderintuition.CoderIntuition.pojos.request.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SolutionDto {
    @NotBlank
    private String name;

    @NotNull
    private Boolean isPrimary;

    @NotNull
    private String description;

    @NotBlank
    private String pythonCode;

    @NotNull
    private String javaCode;

    @NotNull
    private String javascriptCode;
}
