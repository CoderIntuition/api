package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VerifyEmailStatus {
    @JsonProperty("SUCCESS") SUCCESS,
    @JsonProperty("ALREADY") ALREADY,
    @JsonProperty("FAILED") FAILED,
}
