package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.Difficulty;
import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import com.coderintuition.CoderIntuition.models.Solution;
import com.coderintuition.CoderIntuition.models.TestCase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemResponse {
    @NotNull
    private Long id;

    @JsonIgnoreProperties("problem")
    @NotEmpty
    private List<ProblemStep> problemSteps;

    @JsonIgnoreProperties("problem")
    @NotNull
    private TestCase defaultTestCase;

    @JsonIgnoreProperties("problem")
    @NotEmpty
    private List<Solution> solutions;

    @NotBlank
    private String name;

    @NotBlank
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

    public static ProblemResponse fromProblem(Problem problem) {
        ProblemResponse problemResponse = new ProblemResponse();
        problemResponse.setId(problem.getId());
        problemResponse.setProblemSteps(problem.getOrderedProblemSteps());
        problemResponse.setDefaultTestCase(problem.getDefaultTestCase());
        problemResponse.setSolutions(problem.getOrderedSolutions());
        problemResponse.setName(problem.getName());
        problemResponse.setUrlName(problem.getUrlName());
        problemResponse.setPlusOnly(problem.getPlusOnly());
        problemResponse.setCategory(problem.getCategory());
        problemResponse.setDifficulty(problem.getDifficulty());
        problemResponse.setDescription(problem.getDescription());
        problemResponse.setPythonCode(problem.getPythonCode());
        problemResponse.setJavaCode(problem.getJavaCode());
        problemResponse.setJavascriptCode(problem.getJavascriptCode());
        return problemResponse;
    }
}
