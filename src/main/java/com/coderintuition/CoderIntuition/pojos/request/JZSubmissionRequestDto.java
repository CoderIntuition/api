package com.coderintuition.CoderIntuition.pojos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JZSubmissionRequestDto {
    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("language_id")
    private Integer languageId;

    private String stdin;

    @JsonProperty("callback_url")
    private String callbackUrl = "https://api.coderintuition.com/submission/judge0callback";
}
