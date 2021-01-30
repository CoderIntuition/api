package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.enums.AuthProvider;
import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.enums.VerifyEmailStatus;
import com.coderintuition.CoderIntuition.exceptions.BadRequestException;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.exceptions.ResourceNotFoundException;
import com.coderintuition.CoderIntuition.models.Activity;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.pojos.request.ChangePasswordRequest;
import com.coderintuition.CoderIntuition.pojos.request.UserGeneralSettingsRequest;
import com.coderintuition.CoderIntuition.pojos.request.VerifyEmailRequest;
import com.coderintuition.CoderIntuition.pojos.response.UserProfileResponseDto;
import com.coderintuition.CoderIntuition.pojos.response.UserResponse;
import com.coderintuition.CoderIntuition.pojos.response.VerifyEmailResponse;
import com.coderintuition.CoderIntuition.repositories.ActivityRepository;
import com.coderintuition.CoderIntuition.repositories.RoleRepository;
import com.coderintuition.CoderIntuition.repositories.SubmissionRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/user/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserResponse getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        // check if plus subscription expired
        Role plusRole = roleRepository.findByName(ERole.ROLE_PLUS).orElseThrow();
        if (user.getRoles().contains(plusRole) && user.getPlusExpirationDate().before(new Date())) {
            Set<Role> roles = user.getRoles();
            roles.remove(plusRole);
            user.setRoles(roles);
            userRepository.save(user);
        }

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getEmail(),
            user.getVerified(),
            user.getImageUrl(),
            user.getLanguage(),
            user.getRoles(),
            user.getPoints(),
            user.getGithubLink(),
            user.getLinkedinLink(),
            user.getWebsiteLink()
        );
    }

    @GetMapping(value = "/user/{username}")
    public UserProfileResponseDto getUserProfile(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RecordNotFoundException("Username " + username + " not found"));

        Boolean plusRole = user.getRoles()
            .stream()
            .anyMatch(role -> role.getName().equals(ERole.ROLE_PLUS));

        int numCompletedProblems = submissionRepository.findNumOfCompletedProblemsByUser(user);

        List<Activity> activities = activityRepository.findActivitiesByUser(user);

        return new UserProfileResponseDto(user.getName(),
            user.getUsername(),
            plusRole,
            user.getImageUrl(),
            numCompletedProblems,
            user.getBadges(),
            activities,
            // TODO: call level calculation method to calculate level
            0,
            user.getGithubLink(),
            user.getLinkedinLink(),
            user.getWebsiteLink(),
            user.getCreated_at());
    }

    @PostMapping("/user/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void saveUserGeneralInfo(@CurrentUser UserPrincipal userPrincipal,
                                    @RequestBody UserGeneralSettingsRequest settingsRequest) {
        User userResult = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        userResult.setName(settingsRequest.getName());
        userResult.setUsername(settingsRequest.getUsername());
        userResult.setGithubLink(settingsRequest.getGithubLink());
        userResult.setLinkedinLink(settingsRequest.getLinkedinLink());
        userResult.setWebsiteLink(settingsRequest.getWebsiteLink());
        userRepository.save(userResult);
    }

    @PostMapping("/user/me/changepassword")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void changePassword(@CurrentUser UserPrincipal userPrincipal,
                               @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            String provider = StringUtils.capitalize(user.getAuthProvider().toString().toLowerCase());
            throw new BadRequestException("Your account is with " + provider + ". Please change your password on "
                + provider + "'s website instead.");
        }
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @PostMapping("/user/verify")
    public VerifyEmailResponse verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest) {
        Optional<User> userOptional = userRepository.findByUuid(verifyEmailRequest.getUuid());
        VerifyEmailResponse response = new VerifyEmailResponse();
        if (userOptional.isEmpty()) {
            response.setStatus(VerifyEmailStatus.FAILED);
            response.setName("");
        } else {
            User user = userOptional.get();
            if (!user.getVerified()) {
                user.setVerified(true);
                userRepository.save(user);
                response.setStatus(VerifyEmailStatus.SUCCESS);
            } else {
                response.setStatus(VerifyEmailStatus.ALREADY);
            }
            response.setName(user.getName());
        }
        return response;
    }

    @GetMapping("/user/me/submissions/{problemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Submission> getSubmissions(@CurrentUser UserPrincipal userPrincipal,
                                           @PathVariable Long problemId) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        return user.getSubmissions().stream()
            .filter((x) -> x.getProblem().getId().equals(problemId))
            .collect(Collectors.toList());
    }

    @GetMapping("/user/{username}/completedProblems")
    int getStatsSubmission(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RecordNotFoundException("Username " + username + " not found"));

        return submissionRepository.findNumOfCompletedProblemsByUser(user);
    }
}