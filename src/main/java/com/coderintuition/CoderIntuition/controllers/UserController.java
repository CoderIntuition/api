package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.exceptions.BadRequestException;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.exceptions.ResourceNotFoundException;
import com.coderintuition.CoderIntuition.models.ERole;
import com.coderintuition.CoderIntuition.enums.AuthProvider;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.pojos.request.ChangePasswordRequest;
import com.coderintuition.CoderIntuition.pojos.request.UserGeneralSettingsRequest;
import com.coderintuition.CoderIntuition.pojos.response.UserProfileResponseDto;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    @GetMapping(value = "/user/{username}")
    public UserProfileResponseDto getUserProfile(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordNotFoundException("Username " + username + " not found"));

        Boolean plusRole = user.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_PLUS));

        return new UserProfileResponseDto(user.getName(),
                user.getUsername(),
                plusRole,
                user.getImageUrl(),
                user.getBadges(),
                user.getActivities(),
                // TODO: call level calculation method to calculate level
                0,
                user.getGithubLink(),
                user.getLinkedinLink(),
                user.getWebsiteLink(),
                user.getCreated_at());
    }

    @PostMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('USER')")
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

    @GetMapping("/user/me/submissions/{problemId}")
    @PreAuthorize("hasRole('USER')")
    public List<Submission> getSubmissions(@CurrentUser UserPrincipal userPrincipal,
                                           @PathVariable Long problemId) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        return user.getSubmissions().stream()
                .filter((x) -> x.getProblem().getId().equals(problemId))
                .collect(Collectors.toList());
    }

}