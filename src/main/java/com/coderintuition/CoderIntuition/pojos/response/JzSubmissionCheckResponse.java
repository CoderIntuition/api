package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.common.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JzSubmissionCheckResponse {
    @JsonProperty("compile_output")
    private String compileOutput;
    private int memory;
    private String message;
    private JzResponseStatus status;
    private String stderr;
    private String stdout;
    private String time;
    private String token;

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }
}
