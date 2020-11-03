package com.coderintuition.CoderIntuition.dtos.request.cms;

import com.coderintuition.CoderIntuition.models.ProblemStepType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProblemStepDto {
    private String name;
    private ProblemStepType type;
    private String content;
    private Integer time;
}
