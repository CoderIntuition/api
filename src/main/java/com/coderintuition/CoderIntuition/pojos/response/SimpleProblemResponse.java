package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.Difficulty;
import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import com.coderintuition.CoderIntuition.models.Problem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SimpleProblemResponse {
    @NotNull
    private Long id;

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

    public static SimpleProblemResponse fromProblem(Problem problem) {
        SimpleProblemResponse simpleProblemResponse = new SimpleProblemResponse();
        simpleProblemResponse.setId(problem.getId());
        simpleProblemResponse.setName(problem.getName());
        simpleProblemResponse.setUrlName(problem.getUrlName());
        simpleProblemResponse.setPlusOnly(problem.getPlusOnly());
        simpleProblemResponse.setCategory(problem.getCategory());
        simpleProblemResponse.setDifficulty(problem.getDifficulty());
        return simpleProblemResponse;
    }
}
