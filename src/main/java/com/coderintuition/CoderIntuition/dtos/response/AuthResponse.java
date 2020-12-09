package com.coderintuition.CoderIntuition.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long expiration;
    private String type = "Bearer";

    public AuthResponse(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }
}
