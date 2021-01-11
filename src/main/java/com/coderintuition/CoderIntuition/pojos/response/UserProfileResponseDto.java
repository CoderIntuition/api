package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.models.Activity;
import com.coderintuition.CoderIntuition.models.Badge;
import com.coderintuition.CoderIntuition.models.Role;
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
public class UserProfileResponseDto {
    private String name;
    private String username;
    private Boolean plusRole;
    private String profilePicturePath;
    private int numCompletedProblems;
    private List<Badge> badges;
    private List<Activity> activities;
    private Integer level;
    private String githubLink;
    private String linkedinLink;
    private String websiteLink;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date joinedDate;
}

