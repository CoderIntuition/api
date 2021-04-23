package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
public class ProduceOutputDto {
    @NotNull
    private Long problemId;

    @NotNull
    @PositiveOrZero
    private Integer testCaseNum;

    @NotNull
    private String input;

    @NotNull
    private Language language;

    @NotBlank
    private String code;

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }
}
