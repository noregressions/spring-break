# Spring Security Access Relocated (Tier 1: Won't Build)

**Summary**: The legacy Access API (`AccessDecisionManager`, `AccessDecisionVoter`, `AffirmativeBased`, `RoleVoter`, etc.) has been split out of `spring-security-core` into a new standalone `spring-security-access` artifact in Spring Security 7.0. Code referencing those classes fails to compile on Boot 4.0 unless the new module is added explicitly.

**Sibling concern (NOT covered by this module)**: `@EnableGlobalMethodSecurity` is sometimes assumed to be part of this change — it isn't. That annotation is still in `spring-security-config` (where it has always been), deprecated since Spring Security 5.6 (Boot 2.7) but still functional in 7.x. No `pom.xml` change required to keep it compiling. Migration to `@EnableMethodSecurity` is recommended but separate.

## What Breaks

`spring-security-core` on Spring Security 7.0 no longer ships the `org.springframework.security.access.*` classes. Any code importing `AccessDecisionManager`, `AccessDecisionVoter`, `AffirmativeBased`, `RoleVoter`, `ConsensusBased`, or `UnanimousBased` directly (and only declaring `spring-security-core` / `spring-boot-starter-security` as a dependency) fails at `javac`.

## How This Test Works

`AccessApiUsage.java` imports `AccessDecisionManager`, `AffirmativeBased`, and `RoleVoter`, and calls `new AffirmativeBased(...)` to force the symbols to resolve.

The module declares only `spring-boot-starter-security` (no `spring-security-access`) — mirroring a typical Boot 3.5 application that gets the access classes transitively via `spring-security-core`.

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Builds and tests pass. The access classes are in `spring-security-core`.

## On Spring Boot 4.0

```bash
mvn compile -Dspring-boot.version=4.0.6
```

**Result**: ✗ Build fails at compile.

**Error**:
```
[ERROR] AccessApiUsage.java:[3,43] cannot find symbol
  symbol:   class AccessDecisionManager
  location: package org.springframework.security.access
[ERROR] AccessApiUsage.java:[4,48] package org.springframework.security.access.vote does not exist
[ERROR] AccessApiUsage.java:[5,48] package org.springframework.security.access.vote does not exist
```

`javac` cannot find the symbols; the build halts before tests run.

## Fix / Migration Path

Add the relocated module to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-access</artifactId>
</dependency>
```

Empirically verified: with this dependency in place against Boot 4.0.6, `AccessApiUsage.java` compiles successfully (with `uses or overrides a deprecated API` warnings — the legacy classes are deprecated but functional).

The long-term migration is to `AuthorizationManager<RequestAuthorizationContext>`. The `spring-security-access` module is provided to keep teams unblocked during that migration, not as a permanent home.

## References

- [Spring Blog: Access API Moves to Spring Security Access](https://spring.io/blog/2025/09/09/access-api-moves-to-spring-security-access)
- [Spring Security 7 Migration Guide](https://docs.spring.io/spring-security/reference/migration-7/index.html)
- Master list entry: 1.62
