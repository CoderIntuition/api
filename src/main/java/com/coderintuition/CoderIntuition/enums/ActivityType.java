package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActivityType {
    @JsonProperty("LEARN_INTUITION") LEARN_INTUITION,
    @JsonProperty("EARN_BADGE") EARN_BADGE,
    @JsonProperty("UPGRADE_PLUS") UPGRADE_PLUS,
    @JsonProperty("SUBMIT_PROBLEM") SUBMIT_PROBLEM
}
