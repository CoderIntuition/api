package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Difficulty {
    @JsonProperty("BEGINNER") BEGINNER,
    @JsonProperty("EASY") EASY,
    @JsonProperty("MEDIUM") MEDIUM,
    @JsonProperty("HARD") HARD,
}
