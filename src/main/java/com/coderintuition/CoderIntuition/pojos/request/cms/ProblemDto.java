package com.coderintuition.CoderIntuition.pojos.request.cms;

import com.coderintuition.CoderIntuition.enums.Category;
import com.coderintuition.CoderIntuition.enums.Difficulty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
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

    private Category category;

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
