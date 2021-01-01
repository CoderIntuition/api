package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActivityType {
    @JsonProperty("START_PROBLEM") START_PROBLEM,
    @JsonProperty("EARN_BADGE") EARN_BADGE,
    @JsonProperty("UPGRADE_PLUS") UPGRADE_PLUS,
    @JsonProperty("LOGIN") LOGIN
}
