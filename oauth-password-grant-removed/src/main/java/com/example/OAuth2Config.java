package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * OAuth2 Configuration demonstrating the Password Grant setup.
 *
 * This configuration is designed to show how the Password Grant was configured
 * in Spring Security 6.0 and earlier. In Spring Security 6.1+ and Spring Boot 4.0,
 * the Password Grant is completely removed.
 */
@Configuration
public class OAuth2Config {

    /**
     * This bean demonstrates the Password Grant registration that will no longer
     * work in Spring Boot 4.0.
     *
     * In Spring Security 6.0, this would configure:
     * - Authorization Grant Type: password
     * - Client credentials authentication
     *
     * In Spring Security 6.1+, attempts to use password grant will fail during
     * client registration processing because the grant type is no longer supported.
     */
    @Bean
    public InMemoryClientRegistrationRepository clientRegistrationRepository() {
        // This ClientRegistration uses the deprecated password grant type
        // It will be created here but may cause issues when actually used
        ClientRegistration passwordGrant = ClientRegistration
                .withRegistrationId("example")
                .clientId("my-client-id")
                .clientSecret("my-client-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // WARNING: This grant type is removed in Spring Security 6.1+
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .tokenUri("https://example.com/oauth/token")
                .scope("read", "write")
                .build();

        return new InMemoryClientRegistrationRepository(passwordGrant);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository()
        );
    }
}
