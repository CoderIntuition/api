package com.coderintuition.CoderIntuition.pojos.request.cms;

import com.coderintuition.CoderIntuition.enums.Difficulty;
import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemDto {
    @NotBlank
    @Size(max = 300)
    private String name;

    @NotBlank
    @Size(max = 300)
    private String urlName;

    @NotNull
    private Boolean plusOnly;

    @NotNull
    private ProblemCategory category;

    @NotNull
    private Difficulty difficulty;

    @NotBlank
    private String description;

    @NotBlank
    private String pythonCode;

    @NotNull
    private String javaCode;

    @NotNull
    private String javascriptCode;

    @NotEmpty
    private List<ProblemStepDto> problemSteps;

    @NotEmpty
    private List<TestCaseDto> testCases;

    @NotEmpty
    private List<SolutionDto> solutions;

    @NotEmpty
    private List<ArgumentDto> arguments;

    @NotNull
    private ReturnTypeDto returnType;
}
