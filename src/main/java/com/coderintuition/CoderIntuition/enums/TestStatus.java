package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TestStatus {
    @JsonProperty("PASSED") PASSED,
    @JsonProperty("FAILED") FAILED,
    @JsonProperty("ERROR") ERROR,
    @JsonProperty("RUNNING") RUNNING
}
