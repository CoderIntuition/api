package com.coderintuition.CoderIntuition.pojos.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserGeneralSettingsRequest {
    private String name;
    private String username;
    private String githubLink;
    private String linkedinLink;
    private String websiteLink;
    private String language;
}
