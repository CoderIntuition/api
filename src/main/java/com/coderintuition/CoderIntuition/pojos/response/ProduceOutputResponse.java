package com.coderintuition.CoderIntuition.pojos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProduceOutputResponse {
    private String status;
    private String output;
    private String stdout;
    private String stderr;
}
