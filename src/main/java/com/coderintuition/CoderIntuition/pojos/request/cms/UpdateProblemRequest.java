package com.coderintuition.CoderIntuition.pojos.request.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProblemRequest {
    @NotNull
    private Long id;

    @NotNull
    private ProblemDto problem;
}
