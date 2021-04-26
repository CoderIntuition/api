package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RunRequestDto {
    @NotNull
    private Long problemId;

    @NotNull
    private String sessionId;

    private Long userId;

    @NotNull
    private Language language;

    @NotNull
    private String input;

    @NotBlank
    private String code;

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }
}
