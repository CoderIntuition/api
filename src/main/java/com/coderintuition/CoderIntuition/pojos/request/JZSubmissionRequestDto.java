package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.common.Utils;
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
    private String callbackUrl;

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }
}
