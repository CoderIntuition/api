package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProblemStepType {
    @JsonProperty("TEXT") TEXT,
    @JsonProperty("QUIZ") QUIZ
}
