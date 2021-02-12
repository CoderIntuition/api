package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.enums.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ProduceOutputDto {
    @NotNull
    private Long problemId;

    @NotNull
    private String input;

    @NotNull
    private Language language;

    @NotBlank
    private String code;
}
