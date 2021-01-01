package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AuthProvider {
    @JsonProperty("LOCAL") LOCAL,
    @JsonProperty("FACEBOOK") FACEBOOK,
    @JsonProperty("GOOGLE") GOOGLE,
    @JsonProperty("GITHUB") GITHUB
}
