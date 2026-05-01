package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (c) — Runtime Errors on 4.0
 *
 * Demonstrates the PKCE default change for confidential OAuth2 clients.
 *
 * Spring Security 6.x (Spring Boot 3.5):
 *   PKCE (Proof Key for Code Exchange) is only applied to public clients
 *   (those without a client_secret). Confidential clients use the standard
 *   authorization code flow without code_challenge / code_verifier.
 *
 * Spring Security 7.x (Spring Boot 4.0):
 *   PKCE is enabled by default for ALL clients, including confidential ones.
 *   The authorization request automatically includes code_challenge and
 *   code_challenge_method parameters. Legacy OAuth providers that do not
 *   support PKCE will reject the request (e.g. "unsupported_parameter").
 *
 * This test creates a confidential client (with a client_secret) and
 * verifies that the authorization request does NOT include PKCE parameters.
 * On Boot 3.5 this passes; on Boot 4.0 the assertion fails because PKCE
 * params are now present.
 *
 * Fix: Ensure your OAuth provider supports PKCE (most modern providers do),
 *   or explicitly disable PKCE via a custom OAuth2AuthorizationRequestResolver.
 *
 * References:
 * - PKCE RFC 7636: https://datatracker.ietf.org/doc/html/rfc7636
 * - OAuth 2.1 (RFC 9700): https://datatracker.ietf.org/doc/html/rfc9700
 * - Spring Security 7.0 Migration: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
 */
class PkceTest {

    @Test
    void confidentialClientShouldNotIncludePkceByDefault() {
        // Build a confidential client registration (has a client_secret)
        ClientRegistration registration = ClientRegistration.withRegistrationId("test-provider")
                .clientId("my-confidential-client")
                .clientSecret("super-secret-value")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/test-provider")
                .authorizationUri("https://auth.example.com/oauth/authorize")
                .tokenUri("https://auth.example.com/oauth/token")
                .scope("openid", "profile")
                .build();

        InMemoryClientRegistrationRepository repo =
                new InMemoryClientRegistrationRepository(registration);

        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");

        // Simulate a request to initiate OAuth2 login for our confidential client
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/oauth2/authorization/test-provider");
        request.setServletPath("/oauth2/authorization/test-provider");

        OAuth2AuthorizationRequest authzRequest = resolver.resolve(request);
        assertNotNull(authzRequest, "Authorization request should be resolved");

        Map<String, Object> params = authzRequest.getAdditionalParameters();

        // Spring Security 6.x (Boot 3.5):
        //   Confidential clients do NOT get PKCE parameters → passes
        //
        // Spring Security 7.x (Boot 4.0):
        //   ALL clients get PKCE by default → code_challenge is present → FAILS
        //
        // A legacy OAuth provider that doesn't support PKCE would reject
        // the request with "unsupported_parameter" or similar error.
        assertFalse(
                params.containsKey("code_challenge"),
                "Confidential client authorization request should NOT contain " +
                "code_challenge on Security 6.x (Boot 3.5). " +
                "On Security 7.x (Boot 4.0), PKCE is enabled by default for ALL " +
                "clients. Legacy OAuth providers that don't support PKCE will " +
                "reject this request."
        );
    }
}
