package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.dtos.response.UserProfileResponseDto;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.exceptions.ResourceNotFoundException;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

        return new UserProfileResponseDto(user.getName(),
                user.getUsername(),
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
}