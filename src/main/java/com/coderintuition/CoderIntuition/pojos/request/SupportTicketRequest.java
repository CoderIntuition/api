package com.coderintuition.CoderIntuition.pojos.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SupportTicketRequest {
    @Email
    @NotBlank
    @Size(max = 300)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String subject;

    @NotBlank
    @Size(max = 2000)
    private String message;
}
