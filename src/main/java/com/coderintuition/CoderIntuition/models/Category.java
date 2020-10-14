package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Category {
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
