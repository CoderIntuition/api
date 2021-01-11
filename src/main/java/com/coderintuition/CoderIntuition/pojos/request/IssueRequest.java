package com.coderintuition.CoderIntuition.pojos.request;

import com.coderintuition.CoderIntuition.enums.IssueCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class IssueRequest {
    private Long problemId;

    @Email
    @Max(300)
    @NotBlank
    private String email;

    private IssueCategory category;

    @NotBlank
    @Size(max = 2000)
    private String description;
}
