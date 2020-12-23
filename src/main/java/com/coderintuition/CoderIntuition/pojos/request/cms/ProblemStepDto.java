package com.coderintuition.CoderIntuition.pojos.request.cms;

import com.coderintuition.CoderIntuition.models.ProblemStepType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class ProblemStepDto {
    @NotBlank
    @Size(max = 300)
    private String name;

    private ProblemStepType type;

    @NotBlank
    private String content;

    @NotNull
    @PositiveOrZero
    private Integer time;
}
