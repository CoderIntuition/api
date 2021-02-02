package com.coderintuition.CoderIntuition.security.oauth2;

import com.coderintuition.CoderIntuition.pojos.response.GithubEmailResponse;
import com.coderintuition.CoderIntuition.exceptions.OAuth2AuthenticationProcessingException;
import com.coderintuition.CoderIntuition.enums.AuthProvider;
import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.repositories.RoleRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import com.coderintuition.CoderIntuition.security.oauth2.user.OAuth2UserInfo;
import com.coderintuition.CoderIntuition.security.oauth2.user.OAuth2UserInfoFactory;
import com.coderintuition.CoderIntuition.common.Utils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private static final Gson GSON = new Gson();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        LOGGER.debug("oAuth2UserRequest={}", GSON.toJson(oAuth2UserRequest));
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        LOGGER.debug("oAuth2User={}", GSON.toJson(oAuth2User));
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        Map<String, Object> unprotectedAttributes = new HashMap<>(oAuth2User.getAttributes());
        LOGGER.debug("unprotectedAttributes={}", GSON.toJson(unprotectedAttributes));
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), unprotectedAttributes);

        AuthProvider authProvider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
        String authProviderName = StringUtils.capitalize(authProvider.toString().toLowerCase());
        LOGGER.debug("authProviderName={}", authProviderName);

        // github doesn't provide emails from oauth so need to fetch from their API
        if (authProvider == AuthProvider.GITHUB && (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isEmpty())) {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "token " + oAuth2UserRequest.getAccessToken().getTokenValue());

            Flux<GithubEmailResponse> response = WebClient
                    .create("https://api.github.com")
                    .get()
                    .uri("/user/emails")
                    .headers(httpHeaders -> httpHeaders.setAll(header))
                    .retrieve()
                    .bodyToFlux(GithubEmailResponse.class);

            GithubEmailResponse firstEmail = response.blockFirst(Duration.ofSeconds(5));
            if (firstEmail != null) {
                oAuth2UserInfo.setEmail(firstEmail.getEmail());
            }
        }

        // no email received from auth provider
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not received from OAuth2 provider " + authProviderName);
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        // if user already has an account try to update, otherwise create new account
        if (userOptional.isPresent()) {
            LOGGER.debug("User already has an account so update the user");
            user = userOptional.get();
            // user has an account using a different provider
            if (!user.getAuthProvider().equals(authProvider)) {
                String userAuthProviderName = StringUtils.capitalize(user.getAuthProvider().toString().toLowerCase());
                throw new OAuth2AuthenticationProcessingException("You already have an existing account with " +
                        userAuthProviderName + ". Please use your " + userAuthProviderName + " account to sign in.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            LOGGER.debug("Create new user");
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setAuthProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setAuthProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setUsername(Utils.generateUsername());
        user.setVerified(false);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow());
        user.setRoles(roles);
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}