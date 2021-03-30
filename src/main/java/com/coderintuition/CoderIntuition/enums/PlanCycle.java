package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PlanCycle {
    @JsonProperty("MONTHLY") MONTHLY,
    @JsonProperty("YEARLY") YEARLY
}
