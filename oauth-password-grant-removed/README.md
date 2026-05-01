# OAuth 2.0 Password Grant Removed

AuthorizationGrantType.PASSWORD removed per OAuth 2.1 security best practices; migrate to Authorization Code or Client Credentials.

## What Breaks

Spring Security 6.1+ (Spring Boot 4.0) removes `AuthorizationGrantType.PASSWORD` and rejects any attempt to use the password grant type. Applications with password grant configuration fail at startup.

**Configuration that breaks:**
```properties
spring.security.oauth2.client.registration.example.authorization-grant-type=password
```

**Code that breaks:**
```java
AuthorizationGrantType.PASSWORD  // NoSuchFieldError
```

**Errors on Spring Boot 4.0:**
- `NoSuchFieldError: PASSWORD`
- `IllegalArgumentException: password grant type is not supported`
- Application fails to start if password grant is configured

## Why It Was Removed

OAuth 2.1 (RFC 9700) explicitly deprecates the password grant because it violates core OAuth security principles:

1. **User credentials exposed**: Requires users to share passwords directly with the application (not an OAuth client)
2. **No refresh tokens**: Often implemented without refresh token rotation
3. **Poor for public clients**: Not suitable for mobile/SPA applications
4. **Security anti-pattern**: Direct password handling contradicts OAuth 2.0/2.1 design

## How This Test Works

The test attempts to access `AuthorizationGrantType.PASSWORD` and verify the OAuth2 configuration:

- **testPasswordGrantConfigurationExists()**: Checks if ClientRegistrationRepository loads successfully. Passes on 3.5.14, likely fails on 4.0 if password grant is configured.
- **testClientRegistrationForPasswordGrant()**: Retrieves the "example" client registration from configuration. Verifies basic OAuth2 client setup.
- **testPasswordGrantUnsupported()**: Attempts to access AuthorizationGrantType.PASSWORD. Passes on 3.5 (field exists), fails on 4.0 (field removed).

## On Spring Boot 3.5.14

```bash
mvn clean test
```

Output: Tests pass. PASSWORD grant type exists and is accessible.

## On Spring Boot 4.0

Tests fail during application startup if configuration uses password grant:
```
IllegalArgumentException: password grant type is not supported
```

Or at field access:
```
NoSuchFieldError: PASSWORD
```

## Fix / Migration Path

Identify your use case and migrate to an appropriate OAuth 2.0 / 2.1 grant type:

**1. Audit configuration:**
```bash
grep -r "authorization-grant-type.*password" src/main/resources/
grep -r "AuthorizationGrantType.PASSWORD" src/
```

**2. Migrate based on use case:**

**User Interactive Login (Web/SPA/Mobile) — Use Authorization Code with PKCE:**
```properties
spring.security.oauth2.client.registration.myapp.client-id=your-client-id
spring.security.oauth2.client.registration.myapp.client-secret=your-secret
spring.security.oauth2.client.registration.myapp.authorization-grant-type=authorization_code
spring.security.oauth2.client.provider.myapp.authorization-uri=https://auth-server.com/oauth/authorize
spring.security.oauth2.client.provider.myapp.token-uri=https://auth-server.com/oauth/token
spring.security.oauth2.client.provider.myapp.user-info-uri=https://auth-server.com/oauth/userinfo
```

**Server-to-Server (Backend) — Use Client Credentials:**
```properties
spring.security.oauth2.client.registration.backend.client-id=your-client-id
spring.security.oauth2.client.registration.backend.client-secret=your-secret
spring.security.oauth2.client.registration.backend.authorization-grant-type=client_credentials
spring.security.oauth2.client.provider.backend.token-uri=https://auth-server.com/oauth/token
```

**Code migration example:**

Before (Spring Boot 3.5):
```java
ClientRegistration registration = ClientRegistration
    .withRegistrationId("api")
    .clientId("client-id")
    .authorizationGrantType(AuthorizationGrantType.PASSWORD)
    .tokenUri("https://api.example.com/token")
    .build();
```

After (Spring Boot 4.0):
```java
ClientRegistration registration = ClientRegistration
    .withRegistrationId("api")
    .clientId("client-id")
    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
    .authorizationUri("https://api.example.com/authorize")
    .tokenUri("https://api.example.com/token")
    .redirectUri("http://localhost:8080/login/oauth2/code/api")
    .userInfoUri("https://api.example.com/userinfo")
    .build();
```

## References

- Spring Security 7.0 Migration: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
- OAuth 2.1 Security Best Current Practice: https://datatracker.ietf.org/doc/html/draft-ietf-oauth-security-topics
- OAuth 2.0 Password Grant Removal: https://datatracker.ietf.org/doc/html/rfc9700
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
