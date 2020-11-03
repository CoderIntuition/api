package com.coderintuition.CoderIntuition.dtos.request.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolutionDto {
    private String name;
    private Boolean isPrimary;
    private String description;
    private String pythonCode;
    private String javaCode;
    private String javascriptCode;
}
