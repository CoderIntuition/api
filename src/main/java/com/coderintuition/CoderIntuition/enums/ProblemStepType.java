package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProblemStepType {
    @JsonProperty("TEXT") TEXT,
    @JsonProperty("QUIZ") QUIZ
}
