package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.enums.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunRequestDto {
    private Long userId;
    private Long problemId;
    private Language language;
    private String input;
    private String code;
}
