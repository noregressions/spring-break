# PKCE Mandatory OAuth2 Migration Test

## One-Line Summary
Spring Security 7.0 (Spring Boot 4.0) enables PKCE by default for all OAuth2 confidential clients, silently breaking flows with legacy providers that don't support PKCE.

## What Breaks

Spring Security 7.x bundled with Spring Boot 4.0 enables PKCE (Proof Key for Code Exchange) by default for all OAuth2 authorization code flows, including confidential clients with a client_secret:

**Spring Security 6.x:** PKCE only for public clients (no client_secret); confidential clients use direct token exchange
**Spring Security 7.x:** PKCE for all clients including confidential; `code_challenge` automatically added to authorization requests

This is a **silent breaking change** that manifests only when calling the actual OAuth provider. The application code doesn't change, but framework behavior changes. Older providers reject the request with "invalid PKCE parameters" or silently fail.

## How This Test Works

The test module contains a standard Spring Security OAuth2 configuration without explicit PKCE parameters:

1. **OAuth2ClientConfig.java**: Standard `@EnableWebSecurity` with `oauth2Login()` enabled, no PKCE overrides
2. **application.properties**: OAuth2 client registration with authorization_code flow
3. **PkceApp.java**: Minimal Spring Boot application

The test cannot be run in isolation (it requires a real OAuth provider), but the breaking change is visible by:
- Enabling debug logging and checking for PKCE parameter presence in request logs
- Testing against a legacy OAuth provider that rejects PKCE parameters
- Comparing HTTP request payloads between Spring Boot 3.5.14 and 4.0

## On Spring Boot 3.5.14 (Spring Security 6.x)

```bash
mvn spring-boot:run
```

OAuth2 flow with confidential client (client_secret present):
- No PKCE parameters sent (`code_challenge`, `code_challenge_method`)
- Authorization server receives standard OAuth2 authorization request
- Token exchange succeeds with legacy providers

Example request (debug log):
```
GET /oauth/authorize?client_id=...&redirect_uri=...&response_type=code&scope=... HTTP/1.1
```

## On Spring Boot 4.0 (Spring Security 7.x)

```bash
mvn spring-boot:run -Dspring-boot.version=4.0.6
```

Same OAuth2 configuration now includes PKCE automatically:
- PKCE parameters added: `code_challenge`, `code_challenge_method=S256`
- Authorization server must support PKCE or rejects the request
- Modern providers (Google, GitHub, Azure AD, Okta) accept it
- Legacy providers may return "code_challenge not supported" error

Example request (debug log):
```
GET /oauth/authorize?client_id=...&code_challenge=...&code_challenge_method=S256&... HTTP/1.1
```

## What PKCE Is

PKCE (RFC 7636, "Proof Key for Code Exchange") is an OAuth2 security extension that prevents authorization code interception attacks by:

1. Client generates random `code_verifier` (43-128 chars)
2. Client computes `code_challenge = Base64URL(SHA256(code_verifier))`
3. Client sends `code_challenge` and `code_challenge_method=S256` in authorization request
4. Server returns `authorization_code`
5. Client must send `code_verifier` when exchanging code for token
6. Server verifies `SHA256(code_verifier) == code_challenge`

References:
- PKCE RFC 7636: https://datatracker.ietf.org/doc/html/rfc7636
- OAuth 2.1 (mandates PKCE for all flows): https://datatracker.ietf.org/doc/html/rfc9700

## Fix / Migration Path

### Option 1: Upgrade to a Modern OAuth Provider (Recommended)

Most major providers now support PKCE. Migrate if possible:
- Google OAuth2 (supports PKCE)
- GitHub OAuth2 (supports PKCE)
- Microsoft Azure AD (supports PKCE)
- Okta (supports PKCE)
- Auth0 (supports PKCE)
- Keycloak (supports PKCE)

PKCE is a security best practice and is becoming mandatory in OAuth 2.1.

### Option 2: Disable PKCE for Legacy Providers (Conditional Fix)

If you cannot upgrade your provider, disable PKCE via configuration properties:

```properties
# application.properties
spring.security.oauth2.client.registration.legacy-provider.client-id=...
spring.security.oauth2.client.registration.legacy-provider.client-secret=...
spring.security.oauth2.client.registration.legacy-provider.authorization-grant-type=authorization_code
# Disable PKCE if provider doesn't support it
spring.security.oauth2.client.registration.legacy-provider.use-pkce=false
```

### Option 3: Conditional PKCE Based on Provider

Programmatically control PKCE per provider:

```java
@Configuration
@EnableWebSecurity
public class OAuth2ClientConfig {
    
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${oauth.provider.supports-pkce:true}") boolean supportsPkce) {
        
        ClientRegistration.Builder builder = ClientRegistration
            .withRegistrationId("legacy-provider")
            .clientId("...")
            .clientSecret("...")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        
        if (!supportsPkce) {
            // Configure without PKCE — custom PKCE handler that skips it
        }
        
        return new InMemoryClientRegistrationRepository(builder.build());
    }
}
```

### Option 4: Custom PKCE Handler (Advanced)

For fine-grained control, implement a custom authorization request repository that conditionally disables PKCE:

```java
@Bean
public AuthorizationRequestRepository<OAuth2AuthorizationRequest> 
        authorizationRequestRepository() {
    return new HttpSessionOAuth2AuthorizationRequestRepository() {
        @Override
        public void saveAuthorizationRequest(OAuth2AuthorizationRequest request,
                                             HttpServletRequest httpRequest,
                                             HttpServletResponse httpResponse) {
            // Remove PKCE parameters if needed
            if (!supportsPkce(request.getClientId())) {
                OAuth2AuthorizationRequest updated = 
                    OAuth2AuthorizationRequest.from(request)
                    .attribute("code_challenge", null)
                    .attribute("code_challenge_method", null)
                    .build();
                super.saveAuthorizationRequest(updated, httpRequest, httpResponse);
            } else {
                super.saveAuthorizationRequest(request, httpRequest, httpResponse);
            }
        }
    };
}
```

## References

- PKCE RFC 7636: https://datatracker.ietf.org/doc/html/rfc7636
- OAuth 2.1 (mandates PKCE): https://datatracker.ietf.org/doc/html/rfc9700
- Spring Security 7.0 Migration Guide: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes