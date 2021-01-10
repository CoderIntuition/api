package com.coderintuition.CoderIntuition.pojos.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SavePasswordRequest {
    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;

    @NotBlank
    private String token;
}
