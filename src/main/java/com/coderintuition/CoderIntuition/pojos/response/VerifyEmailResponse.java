package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.VerifyEmailStatus;
import lombok.Data;

@Data
public class VerifyEmailResponse {
    private VerifyEmailStatus status;
    private String name;
}
