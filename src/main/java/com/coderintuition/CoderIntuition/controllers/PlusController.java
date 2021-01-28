package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.repositories.RoleRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlusController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/checkout-session")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void createCheckoutSession(@CurrentUser UserPrincipal userPrincipal) throws Exception {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        Role plusRole = roleRepository.findByName(ERole.ROLE_PLUS).orElseThrow();
        if (user.getRoles().contains(plusRole)) {
            throw new Exception("You are already on the Intuition+ plan");
        }

    }
}
