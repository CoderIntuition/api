package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProduceOutputStatus {
    @JsonProperty("ERROR") ERROR,
    @JsonProperty("SUCCESS") SUCCESS,
}
