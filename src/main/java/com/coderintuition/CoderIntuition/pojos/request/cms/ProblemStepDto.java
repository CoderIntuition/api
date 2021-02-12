package com.coderintuition.CoderIntuition.pojos.request.cms;

import com.coderintuition.CoderIntuition.enums.ProblemStepType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ProblemStepDto {
    @NotBlank
    @Size(max = 300)
    private String name;

    @NotNull
    private ProblemStepType type;

    @NotBlank
    private String content;

    @PositiveOrZero
    private Integer time;
}
