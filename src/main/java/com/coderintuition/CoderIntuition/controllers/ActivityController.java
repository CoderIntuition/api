package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.enums.ActivityType;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.models.Activity;
import com.coderintuition.CoderIntuition.models.Badge;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.pojos.request.ActivityRequestDto;
import com.coderintuition.CoderIntuition.repositories.ActivityRepository;
import com.coderintuition.CoderIntuition.repositories.BadgeRepository;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityController {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    BadgeRepository badgeRepository;

    public void createActivity(ActivityRequestDto activityRequestDto, User user) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setActivityType(activityRequestDto.getActivityType());

        if (activityRequestDto.getActivityType() == ActivityType.LEARN_INTUITION ||
            activityRequestDto.getActivityType() == ActivityType.SUBMIT_PROBLEM) {
            Problem problem = problemRepository.findById(activityRequestDto.getProblemId())
                .orElseThrow(() -> new RecordNotFoundException("Problem with ID" + activityRequestDto.getProblemId() + " not found."));
            activity.setProblem(problem);
        }

        if (activityRequestDto.getActivityType() == ActivityType.EARN_BADGE) {
            Badge badge = badgeRepository.findById(activityRequestDto.getBadgeId())
                .orElseThrow(() -> new RecordNotFoundException("Badge with ID" + activityRequestDto.getBadgeId() + " not found."));
            activity.setBadge(badge);
        }

        activityRepository.save(activity);
    }

    @PostMapping("/activity")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void postActivity(@CurrentUser UserPrincipal userPrincipal, ActivityRequestDto activityRequestDto) {
        User user = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RecordNotFoundException("User with ID" + userPrincipal.getId() + " not found."));
        createActivity(activityRequestDto, user);
    }
}
