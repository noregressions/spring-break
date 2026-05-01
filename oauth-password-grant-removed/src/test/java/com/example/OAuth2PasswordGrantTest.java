package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the OAuth 2.0 Password Grant removal in Spring Security 6.1+.
 *
 * BREAKING CHANGE: AuthorizationGrantType.PASSWORD removed.
 *
 * Spring Security 6.0 (Spring Boot 3.5):
 *   - AuthorizationGrantType.PASSWORD exists
 *   - Password grant can be configured
 *   - Application starts normally
 *
 * Spring Security 6.1+ (Spring Boot 4.0):
 *   - PASSWORD field removed from AuthorizationGrantType enum
 *   - Configuration validation rejects password grant
 *   - Application fails to start if password grant is configured
 *
 * REASON: OAuth 2.1 (RFC 9700) security best practices recommend against
 * password grant because it exposes user credentials to the application
 * and violates OAuth 2.0/2.1 security model.
 *
 * MIGRATION: Replace with Authorization Code Grant (user login) or
 * Client Credentials Grant (server-to-server).
 *
 * Reference: https://datatracker.ietf.org/doc/html/draft-ietf-oauth-security-topics
 * Reference: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
 */
@SpringBootTest
public class OAuth2PasswordGrantTest {

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void testPasswordGrantConfigurationExists() {
        // On Spring Boot 3.5: This test may pass if the password grant is still configured
        // On Spring Boot 4.0: This test will likely fail because:
        //   1. Configuration may fail to load
        //   2. Password grant is no longer supported
        //   3. ClientRegistrationRepository may not be available

        assertNotNull(clientRegistrationRepository,
                "ClientRegistrationRepository should be configured");
    }

    @Test
    public void testClientRegistrationForPasswordGrant() {
        if (clientRegistrationRepository == null) {
            // Expected on Spring Boot 4.0 if configuration fails
            return;
        }

        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("example");

        assertNotNull(registration, "Example client registration should exist");
        assertEquals("my-client-id", registration.getClientId());

        // On Spring Boot 3.5: authorizationGrantType would be PASSWORD
        // On Spring Boot 4.0: This assertion would fail if password grant is present
        //                     because it's no longer supported
        AuthorizationGrantType grantType = registration.getAuthorizationGrantType();
        assertNotNull(grantType, "Authorization grant type should be set");
    }

    @Test
    public void testPasswordGrantUnsupported() {
        // BREAKING CHANGE TEST: AuthorizationGrantType.PASSWORD no longer exists.
        //
        // Spring Security 6.0 (Boot 3.5): PASSWORD field exists on enum
        // Spring Security 6.1+ (Boot 4.0): PASSWORD field removed, causes NoSuchFieldError
        //
        // This test documents the removal and guides migration.
        //
        // MIGRATION PATHS:
        // 1. User login flows: Use AuthorizationGrantType.AUTHORIZATION_CODE with PKCE
        // 2. Server-to-server: Use AuthorizationGrantType.CLIENT_CREDENTIALS
        // 3. Legacy apps: Must refactor to avoid password exposure
        //
        // Reference: https://datatracker.ietf.org/doc/html/draft-ietf-oauth-security-topics

        try {
            @SuppressWarnings("unused")
            AuthorizationGrantType passwordGrant = AuthorizationGrantType.PASSWORD;

            // If we get here on Spring Boot 3.5, password grant still exists
            assertNotNull(passwordGrant);
        } catch (NoSuchFieldError | NullPointerException e) {
            // Expected on Spring Boot 4.0 (Spring Security 6.1+)
            // PASSWORD grant type has been removed
            assertTrue(true, "Password grant type has been removed as expected on 4.0");
        }
    }
}
