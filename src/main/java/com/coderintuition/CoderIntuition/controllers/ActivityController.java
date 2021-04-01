package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.enums.ActivityType;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.models.*;
import com.coderintuition.CoderIntuition.pojos.request.ActivityRequestDto;
import com.coderintuition.CoderIntuition.pojos.response.ActivityListResponse;
import com.coderintuition.CoderIntuition.pojos.response.ActivityResponse;
import com.coderintuition.CoderIntuition.pojos.response.CompletedActivitiesResponse;
import com.coderintuition.CoderIntuition.repositories.*;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ActivityController {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    ReadingRepository readingRepository;

    @Autowired
    BadgeRepository badgeRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    public void createActivity(ActivityRequestDto activityRequestDto, User user) {
        ActivityType activityType = activityRequestDto.getActivityType();

        Activity activity = new Activity();
        activity.setUser(user);
        activity.setActivityType(activityType);

        if (activityType == ActivityType.LEARN_INTUITION || activityType == ActivityType.SUBMIT_PROBLEM) {
            Problem problem = problemRepository.findById(activityRequestDto.getProblemId())
                .orElseThrow(() -> new RecordNotFoundException("Problem with ID" + activityRequestDto.getProblemId() + " not found."));

            if (activityType == ActivityType.SUBMIT_PROBLEM) {
                Submission submission = submissionRepository.findById(activityRequestDto.getSubmissionId())
                    .orElseThrow(() -> new RecordNotFoundException("Submission with ID" + activityRequestDto.getSubmissionId() + " not found."));
                activity.setSubmission(submission);
            }

            if (activityType == ActivityType.LEARN_INTUITION &&
                activityRepository.findActivity(user, problem, ActivityType.LEARN_INTUITION) > 0) {
                return; // already exists intuition activity for this problem for this user
            }
            activity.setProblem(problem);
        }

        if (activityType == ActivityType.COMPLETE_READING) {
            Reading reading = readingRepository.findById(activityRequestDto.getReadingId())
                .orElseThrow(() -> new RecordNotFoundException("Reading with ID" + activityRequestDto.getReadingId() + " not found."));

            if (activityRepository.findActivity(user, reading, ActivityType.COMPLETE_READING) > 0) {
                return; // already exists reading activity for this reading for this user
            }

            activity.setReading(reading);
        }

        if (activityType == ActivityType.EARN_BADGE) {
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

    @GetMapping(value = "/activity/{username}", params = {"page", "size"})
    public ActivityListResponse getUserProfile(@PathVariable String username,
                                               @RequestParam("page") int page,
                                               @RequestParam("size") int size) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RecordNotFoundException("Username " + username + " not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Activity> activities = activityRepository.findActivitiesByUser(user, pageable);

        List<ActivityResponse> activityResponses = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityResponse activityResponse = new ActivityResponse();
            activityResponse.setActivityType(activity.getActivityType());

            if (activity.getActivityType() == ActivityType.LEARN_INTUITION || activity.getActivityType() == ActivityType.SUBMIT_PROBLEM) {
                activityResponse.setProblemName(activity.getProblem().getName());
                activityResponse.setProblemUrl(activity.getProblem().getUrlName());
            }

            if (activity.getActivityType() == ActivityType.SUBMIT_PROBLEM) {
                activityResponse.setSubmissionStatus(activity.getSubmission().getStatus());
            }

            if (activity.getActivityType() == ActivityType.COMPLETE_READING) {
                activityResponse.setReadingName(activity.getReading().getName());
                activityResponse.setReadingUrl(activity.getReading().getUrlName());
            }

            activityResponse.setCreatedDate(activity.getCreated_at());
            activityResponses.add(activityResponse);
        }

//        activityResponses.sort((x1, x2) -> x2.getCreatedDate().compareTo(x1.getCreatedDate()));

        return new ActivityListResponse(activities.getTotalPages(), activityResponses);
    }

    @GetMapping(value = "/activity/completed/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CompletedActivitiesResponse getCompletedActivities(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RecordNotFoundException("User not found"));

        List<String> completedProblems = activityRepository.findCompletedProblemsByUser(user);
        List<String> completedReadings = activityRepository.findCompletedReadingsByUser(user);

        return new CompletedActivitiesResponse(completedProblems, completedReadings);
    }
}
