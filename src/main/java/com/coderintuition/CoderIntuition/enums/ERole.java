package com.coderintuition.CoderIntuition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ERole {
    @JsonProperty("ROLE_USER") ROLE_USER,
    @JsonProperty("ROLE_PLUS") ROLE_PLUS,
    @JsonProperty("ROLE_MODERATOR") ROLE_MODERATOR,
    @JsonProperty("ROLE_ADMIN") ROLE_ADMIN
}
