package com.coderintuition.CoderIntuition.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String type = "Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
