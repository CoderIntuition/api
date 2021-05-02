package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.common.Utils;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SaveRequest {
    @NotNull
    private Long problemId;

    @NotNull
    @Size(max = 50000)
    private String pythonCode;

    @NotNull
    @Size(max = 50000)
    private String javaCode;

    @NotNull
    @Size(max = 50000)
    private String javascriptCode;

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }
}
