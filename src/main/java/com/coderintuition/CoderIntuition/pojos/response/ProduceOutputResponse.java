package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.enums.ProduceOutputStatus;
import com.coderintuition.CoderIntuition.models.ProduceOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
public class ProduceOutputResponse {
    @NotNull
    private Long problemId;

    @NotNull
    @PositiveOrZero
    private Integer testCaseNum;

    @NotNull
    private ProduceOutputStatus status;

    private String output;

    private String stdout;

    private String stderr;

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }

    public static ProduceOutputResponse fromProduceOutput(ProduceOutput produceOutput) {
        ProduceOutputResponse produceOutputResponse = new ProduceOutputResponse();
        produceOutputResponse.setProblemId(produceOutput.getProblem().getId());
        produceOutputResponse.setTestCaseNum(produceOutput.getTestCaseNum());
        produceOutputResponse.setStatus(produceOutput.getStatus());
        produceOutputResponse.setOutput(produceOutput.getOutput());
        produceOutputResponse.setStdout(produceOutput.getStdout());
        produceOutputResponse.setStderr(produceOutput.getStderr());
        return produceOutputResponse;
    }

}
