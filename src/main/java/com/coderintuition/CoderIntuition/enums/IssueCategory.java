package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IssueCategory {
    @JsonProperty("TYPO") TYPO,
    @JsonProperty("FUNCTIONALITY") FUNCTIONALITY,
    @JsonProperty("VISUAL") VISUAL,
    @JsonProperty("ERROR") ERROR,
    @JsonProperty("RUN") RUN,
    @JsonProperty("TEST") TEST,
    @JsonProperty("OTHER") OTHER
}
