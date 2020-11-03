package com.coderintuition.CoderIntuition.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long expiration;
    private Long id;
    private String name;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, Long expiration, Long id, String name, String email, List<String> roles) {
        this.token = token;
        this.expiration = expiration;
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }
}
