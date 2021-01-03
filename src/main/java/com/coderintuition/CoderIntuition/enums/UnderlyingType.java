package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UnderlyingType {
    @JsonProperty("STRING") STRING,
    @JsonProperty("INTEGER") INTEGER,
    @JsonProperty("FLOAT") FLOAT,
    @JsonProperty("BOOLEAN") BOOLEAN,
    @JsonProperty("NONE") NONE
}
