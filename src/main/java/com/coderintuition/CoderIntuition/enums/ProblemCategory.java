package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProblemCategory {
    @JsonProperty("ARRAYS") ARRAYS,
    @JsonProperty("STRINGS") STRINGS,
    @JsonProperty("LINKED_LISTS") LINKED_LISTS,
    @JsonProperty("STACKS") STACKS,
    @JsonProperty("QUEUES") QUEUES,
    @JsonProperty("TREES") TREES,
    @JsonProperty("GRAPHS") GRAPHS,
    @JsonProperty("BIT_MANIPULATION") BIT_MANIPULATION,
    @JsonProperty("MATH") MATH,
    @JsonProperty("BACKTRACKING") BACKTRACKING,
    @JsonProperty("GREEDY") GREEDY,
    @JsonProperty("DYNAMIC_PROGRAMMING") DYNAMIC_PROGRAMMING
}
