package com.coderintuition.CoderIntuition.pojos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GithubEmailResponse {
    private String email;
    private Boolean verified;
    private Boolean primary;
    private String visibility;
}
