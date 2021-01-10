package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.common.Utils;
import com.coderintuition.CoderIntuition.config.AppProperties;
import com.coderintuition.CoderIntuition.enums.AuthProvider;
import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.exceptions.BadRequestException;
import com.coderintuition.CoderIntuition.models.PasswordResetToken;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.pojos.request.*;
import com.coderintuition.CoderIntuition.pojos.response.AuthResponse;
import com.coderintuition.CoderIntuition.pojos.response.MessageResponse;
import com.coderintuition.CoderIntuition.repositories.PasswordResetTokenRepository;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AppProperties appProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

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
        User user = new User(UUID.randomUUID().toString(), signUpRequest.getName(), signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()), false,
                Utils.generateUsername(), AuthProvider.LOCAL, roles);
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // token and expiration time
        Pair<String, Long> tokenPair = tokenProvider.createToken(authentication);

        // send welcome email
        String welcomeEmail = Utils.fileToString("email/welcome.html")
                .replaceAll("\\{\\{name}}", user.getName());
        Utils.sendEmail(appProperties.getMailgun().getKey(), user.getEmail(),
                "Welcome to CoderIntuition!",
                welcomeEmail);

        // send verify email
        String verifyEmail = Utils.fileToString("email/verify.html")
                .replaceAll("\\{\\{name}}", user.getName())
                .replaceAll("\\{\\{action_url}}", "https://coderintuition.com/verify/" + user.getUuid());
        Utils.sendEmail(appProperties.getMailgun().getKey(), user.getEmail(),
                "Verify Your CoderIntuition Account's Email",
                verifyEmail);

        return ResponseEntity.ok(new AuthResponse(tokenPair.getFirst()));
    }

    @PostMapping("/reset/request")
    public void resetPasswordRequest(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        Optional<User> userOptional = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getAuthProvider() != AuthProvider.LOCAL) {
                String provider = reactor.util.StringUtils.capitalize(user.getAuthProvider().toString().toLowerCase());
                throw new BadRequestException("Your account is with " + provider + ". Please change your password on "
                        + provider + "'s website instead.");
            }

            // if exists password reset token then invalidate it first
            Optional<PasswordResetToken> optionalOldToken = passwordResetTokenRepository
                    .findByUserAndExpiryDateAfterAndInvalidatedFalse(user, Calendar.getInstance().getTime());
            if (optionalOldToken.isPresent()) {
                PasswordResetToken oldToken = optionalOldToken.get();
                oldToken.setInvalidated(true);
                passwordResetTokenRepository.save(oldToken);
            }

            // create and save token
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(UUID.randomUUID().toString());
            passwordResetToken.setInvalidated(false);
            passwordResetToken.setUser(user);
            passwordResetToken.setExpiryDate();
            passwordResetTokenRepository.save(passwordResetToken);

            // send reset password email
            String verifyEmail = Utils.fileToString("email/reset.html")
                    .replaceAll("\\{\\{name}}", user.getName())
                    .replaceAll("\\{\\{action_url}}", "https://coderintuition.com/reset/" + passwordResetToken.getToken());
            Utils.sendEmail(appProperties.getMailgun().getKey(), user.getEmail(),
                    "Reset Your CoderIntuition Account's Password",
                    verifyEmail);
        }
    }

    @GetMapping("/reset/validate/{token}")
    public void validateToken(@PathVariable String token) throws Exception {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken.isEmpty() || passwordResetToken.get().getInvalidated()) {
            throw new Exception("Invalid reset password link. Please try clicking the link from your reset password email again.");
        }
        if (passwordResetToken.get().getExpiryDate().before(Calendar.getInstance().getTime())) {
            throw new Exception("Reset password link expired. Please try resetting your password again from the login page.");
        }
    }

    @PostMapping("/reset/save")
    public void savePassword(@RequestBody SavePasswordRequest savePasswordRequest) throws Exception {
        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository.findByToken(savePasswordRequest.getToken());
        if (optionalPasswordResetToken.isEmpty() || optionalPasswordResetToken.get().getInvalidated()) {
            throw new Exception("Failed to save password. Please try clicking the link from your email reset password email again.");
        }
        PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();
        if (passwordResetToken.getExpiryDate().before(Calendar.getInstance().getTime())) {
            throw new Exception("Reset password link expired. Please try resetting your password again from the login page.");
        }

        // update the user's password
        User user = passwordResetToken.getUser();
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            String provider = reactor.util.StringUtils.capitalize(user.getAuthProvider().toString().toLowerCase());
            throw new BadRequestException("Your account is with " + provider + ". Please change your password on "
                    + provider + "'s website instead.");
        }
        user.setPassword(passwordEncoder.encode(savePasswordRequest.getNewPassword()));
        userRepository.save(user);

        // invalidate the token
        passwordResetToken.setInvalidated(true);
        passwordResetTokenRepository.save(passwordResetToken);
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