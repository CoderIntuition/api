package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SubmissionStatus {
    @JsonProperty("ACCEPTED") ACCEPTED,
    @JsonProperty("REJECTED") REJECTED,
    @JsonProperty("ERROR") ERROR
}
