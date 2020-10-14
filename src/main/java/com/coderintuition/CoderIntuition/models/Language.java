package com.coderintuition.CoderIntuition.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Language {
    @JsonProperty("PYTHON") PYTHON,
    @JsonProperty("JAVA") JAVA,
    @JsonProperty("JAVASCRIPT") JAVASCRIPT
}
