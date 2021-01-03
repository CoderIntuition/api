package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Difficulty {
    @JsonProperty("BEGINNER") BEGINNER,
    @JsonProperty("EASY") EASY,
    @JsonProperty("MEDIUM") MEDIUM,
    @JsonProperty("HARD") HARD,
}
