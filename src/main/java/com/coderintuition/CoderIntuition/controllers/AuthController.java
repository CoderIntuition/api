package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.pojos.request.LoginRequest;
import com.coderintuition.CoderIntuition.pojos.request.RenewRequest;
import com.coderintuition.CoderIntuition.pojos.request.SignupRequest;
import com.coderintuition.CoderIntuition.pojos.request.ValidateRequest;
import com.coderintuition.CoderIntuition.pojos.response.AuthResponse;
import com.coderintuition.CoderIntuition.pojos.response.MessageResponse;
import com.coderintuition.CoderIntuition.enums.AuthProvider;
import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.repositories.RoleRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.TokenProvider;
import com.coderintuition.CoderIntuition.common.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        // check if email is using LOCAL auth provider
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isPresent() && user.get().getAuthProvider() != AuthProvider.LOCAL) {
            String provider = StringUtils.capitalize(user.get().getAuthProvider().toString().toLowerCase());
            throw new Exception("Your email is signed up using " + provider + ". Please log in using " + provider + " instead.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // token and expiration time
        Pair<String, Long> tokenPair = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(tokenPair.getFirst()));
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
                Utils.generateUsername(), AuthProvider.LOCAL, roles);
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // token and expiration time
        Pair<String, Long> tokenPair = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(tokenPair.getFirst()));
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