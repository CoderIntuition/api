package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.enums.PlanCycle;
import com.coderintuition.CoderIntuition.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private Boolean verified;
    private String imageUrl;
    private Language language;
    private Set<Role> roles;
    private Long points;
    private String githubLink;
    private String linkedinLink;
    private String websiteLink;
    private PlanCycle planCycle;
}
