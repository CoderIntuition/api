package com.coderintuition.CoderIntuition.dtos.request.cms;

import com.coderintuition.CoderIntuition.models.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemDto {
    private String name;
    private String urlName;
    private Category category;
    private Integer difficulty;
    private String description;
    private String pythonCode;
    private String javaCode;
    private String javascriptCode;
    private List<ProblemStepDto> problemSteps;
    private List<TestCaseDto> testCases;
    private List<SolutionDto> solutions;
}
