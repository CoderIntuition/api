package com.coderintuition.CoderIntuition.dtos.response;

import com.coderintuition.CoderIntuition.models.Activity;
import com.coderintuition.CoderIntuition.models.Badge;
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
    private String profilePicturePath;
    private List<Badge> badges;
    private List<Activity> activities;
    private Integer level;
    private String githubLink;
    private String linkedinLink;
    private String websiteLink;
    private Date joinedDate;
}

