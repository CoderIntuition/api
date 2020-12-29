package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.models.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProduceOutputDto {
    private Long problemId;
    private String input;
    private Language language;
    private String code;
}
