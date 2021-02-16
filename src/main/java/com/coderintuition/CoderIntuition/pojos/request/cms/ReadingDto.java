package com.coderintuition.CoderIntuition.pojos.request.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ReadingDto {
    @NotBlank
    @Size(max = 300)
    private String name;

    @NotBlank
    @Size(max = 300)
    private String urlName;

    @NotNull
    private Boolean plusOnly;

    @NotNull
    private Boolean isQuiz;

    @NotBlank
    private String content;
}
