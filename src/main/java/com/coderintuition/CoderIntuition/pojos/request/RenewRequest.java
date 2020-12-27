package com.coderintuition.CoderIntuition.pojos.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RenewRequest {
    private Long userId;
    private String token;
}
