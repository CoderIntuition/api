package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.models.Badge;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String name;
    private String username;
    private Boolean plusRole;
    private String profilePicturePath;
    private Integer numCompletedProblems;
    private List<Badge> badges;
    private Integer level;
    private String githubLink;
    private String linkedinLink;
    private String websiteLink;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date joinedDate;
}

