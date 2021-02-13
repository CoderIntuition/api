package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.enums.ActivityType;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.models.*;
import com.coderintuition.CoderIntuition.pojos.request.ActivityRequestDto;
import com.coderintuition.CoderIntuition.repositories.*;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    SubmissionRepository submissionRepository;

    public void createActivity(ActivityRequestDto activityRequestDto, User user) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setActivityType(activityRequestDto.getActivityType());

        if (activityRequestDto.getActivityType() == ActivityType.LEARN_INTUITION ||
            activityRequestDto.getActivityType() == ActivityType.SUBMIT_PROBLEM) {
            Problem problem = problemRepository.findById(activityRequestDto.getProblemId())
                .orElseThrow(() -> new RecordNotFoundException("Problem with ID" + activityRequestDto.getProblemId() + " not found."));

            if (activityRequestDto.getActivityType() == ActivityType.SUBMIT_PROBLEM) {
                Submission submission = submissionRepository.findById(activityRequestDto.getSubmissionId())
                    .orElseThrow(() -> new RecordNotFoundException("Submission with ID" + activityRequestDto.getSubmissionId() + " not found."));
                activity.setSubmission(submission);
            }

            if (activityRequestDto.getActivityType() == ActivityType.LEARN_INTUITION &&
                activityRepository.findActivity(user, problem, ActivityType.LEARN_INTUITION) > 0) {
                return;
            }
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
    public void postActivity(@CurrentUser UserPrincipal userPrincipal, @RequestBody ActivityRequestDto activityRequestDto) {
        User user = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RecordNotFoundException("User with ID" + userPrincipal.getId() + " not found."));
        createActivity(activityRequestDto, user);
    }
}
