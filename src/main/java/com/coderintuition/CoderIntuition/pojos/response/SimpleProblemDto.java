package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.Difficulty;
import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleProblemDto {
    private Long id;
    private String name;
    private String urlName;
    private Boolean plusOnly;
    private ProblemCategory category;
    private Difficulty difficulty;
}
