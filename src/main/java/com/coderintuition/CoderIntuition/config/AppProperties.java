package com.coderintuition.CoderIntuition.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
@Getter
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();
    private final Mailgun mailgun = new Mailgun();
    private final Stripe stripe = new Stripe();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMs;
    }

    @Getter
    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Mailgun {
        private String key;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Stripe {
        private String testKey;
        private String liveKey;
        private String webhookSecret;
    }
}
