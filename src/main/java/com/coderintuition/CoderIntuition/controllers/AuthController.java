package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.dtos.request.LoginRequest;
import com.coderintuition.CoderIntuition.dtos.request.RenewRequest;
import com.coderintuition.CoderIntuition.dtos.request.SignupRequest;
import com.coderintuition.CoderIntuition.dtos.request.ValidateRequest;
import com.coderintuition.CoderIntuition.dtos.response.AuthResponse;
import com.coderintuition.CoderIntuition.dtos.response.MessageResponse;
import com.coderintuition.CoderIntuition.models.AuthProvider;
import com.coderintuition.CoderIntuition.models.ERole;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.repositories.RoleRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // token and expiration time
        Pair<String, Long> tokenPair = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(tokenPair.getFirst()));
    }

    private static String generateUsername() {
        String aToZ = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int randIndex = rand.nextInt(aToZ.length());
            res.append(aToZ.charAt(randIndex));
        }
        return res.toString();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use"));
        }
        // Create new user's account
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User user = new User(signUpRequest.getName(), signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()), false,
                generateUsername(), AuthProvider.LOCAL, roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewUser(@RequestBody RenewRequest renewRequest) {
        if (!tokenProvider.validateToken(renewRequest.getToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        Pair<String, Long> tokenPair = tokenProvider.refreshToken(renewRequest.getUserId());

        return ResponseEntity.ok(new AuthResponse(tokenPair.getFirst()));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateUser(@RequestBody ValidateRequest validateRequest) {
        if (tokenProvider.validateToken(validateRequest.getToken())) {
            return ResponseEntity.ok().body("Valid token");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}