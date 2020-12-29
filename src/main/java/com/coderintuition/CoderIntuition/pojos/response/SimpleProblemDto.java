package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.models.Category;
import com.coderintuition.CoderIntuition.models.Difficulty;
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
    private Category category;
    private Difficulty difficulty;
}
