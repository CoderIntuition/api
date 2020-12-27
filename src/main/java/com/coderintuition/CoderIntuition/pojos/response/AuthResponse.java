package com.coderintuition.CoderIntuition.pojos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
