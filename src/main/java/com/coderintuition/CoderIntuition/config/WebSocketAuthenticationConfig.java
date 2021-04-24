package com.coderintuition.CoderIntuition.config;

import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.exceptions.BadRequestException;
import com.coderintuition.CoderIntuition.exceptions.InternalServerException;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.TokenProvider;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private SecuredInterceptor securedInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(securedInterceptor);
    }
}

@Slf4j
@Component
class SecuredInterceptor implements ChannelInterceptor {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new InternalServerException("Failed to get accessor");
        }

        if (accessor.getCommand() == StompCommand.SUBSCRIBE || accessor.getCommand() == StompCommand.SEND) {
            if (accessor.getDestination() == null) {
                throw new InternalServerException("No destination");
            }

            String destination = accessor.getDestination();
            log.info("Received websocket message, command={}, destination={}, payload={}", accessor.getCommand().name(), destination, new Gson().toJson(message.getPayload()));

            List<String> destinationComponents = new LinkedList<>(Arrays.asList(accessor.getDestination().split("/")));
            destinationComponents.remove(0); // index 0 is empty because path starts with slash

            if (destinationComponents.get(0).equals("app")) {
                destinationComponents.remove(0);
            }

            if (destinationComponents.get(0).equals("global") || destinationComponents.get(0).equals("topic")) {
               if (destinationComponents.size() < 2) {
                   throw new BadRequestException("Invalid session id");
               }
            } else if (destinationComponents.get(0).equals("secured")) {
                List<String> authorization = accessor.getNativeHeader("Authorization");
                if (authorization == null || authorization.isEmpty()) {
                    throw new BadRequestException("Invalid authorization headers");
                }

                String authToken = authorization.get(0).split(" ")[1];
                if (authToken.isEmpty() || !tokenProvider.validateToken(authToken)) {
                    throw new BadRequestException("Invalid authorization");
                }

                Long userId = tokenProvider.getUserIdFromToken(authToken);
                User user = userRepository.findById(userId).orElseThrow();

                if (user.getId() != Long.parseLong(destinationComponents.get(1))) {
                    throw new BadRequestException("Invalid authorization");
                }

                if (destination.startsWith("/secured/produceoutput") && !user.hasRole(ERole.ROLE_MODERATOR)) {
                    throw new BadRequestException("Invalid authorization");
                }
            } else {
                throw new BadRequestException("Invalid topic");
            }
        }

        return message;
    }
}
