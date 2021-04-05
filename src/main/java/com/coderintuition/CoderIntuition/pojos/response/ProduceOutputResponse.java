package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.ProduceOutputStatus;
import com.coderintuition.CoderIntuition.models.ProduceOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ProduceOutputResponse {
    @NotNull
    private ProduceOutputStatus status;

    private String output;

    private String stdout;

    private String stderr;

    public static ProduceOutputResponse fromProduceOutput(ProduceOutput produceOutput) {
        ProduceOutputResponse produceOutputResponse = new ProduceOutputResponse();
        produceOutputResponse.setStatus(produceOutput.getStatus());
        produceOutputResponse.setOutput(produceOutput.getOutput());
        produceOutputResponse.setStdout(produceOutput.getStdout());
        produceOutputResponse.setStderr(produceOutput.getStderr());
        return produceOutputResponse;
    }

}
