package com.coderintuition.CoderIntuition.dtos.request.cms;

import com.coderintuition.CoderIntuition.models.*;
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

    @NotNull
    @Min(1)
    @Max(5)
    private Integer difficulty;

    @NotBlank
    private String description;

    @NotBlank
    private String pythonCode;

    private String javaCode;
    private String javascriptCode;

    @NotEmpty
    private List<ProblemStepDto> problemSteps;

    @NotEmpty
    private List<TestCaseDto> testCases;

    @NotEmpty
    private List<SolutionDto> solutions;
}
