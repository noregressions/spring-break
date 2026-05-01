# Spring Security Access Relocated (Tier 1: Won't Compile)

**Summary**: The legacy Access API (`AccessDecisionManager`, `AccessDecisionVoter`, etc.) has been moved to a separate module `spring-security-access` in Spring Security 7.0.

## What breaks

Code that depends on legacy authorization components like `AccessDecisionManager` or `@EnableGlobalMethodSecurity` will fail to compile on Spring Boot 4.0 unless the new `spring-security-access` module is explicitly added.

```java
import org.springframework.security.access.AccessDecisionManager;
```

## How this test works

The module includes a class `AccessApiUsage` that imports and uses `AccessDecisionManager`.

On Boot 3.5: Compiles and runs (included in `spring-security-core`).
On Boot 4.0: Fails to compile because it's no longer in `spring-security-core`.

## Fix / Migration Path

Either migrate to the modern `AuthorizationManager` API or add the legacy module to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-access</artifactId>
</dependency>
```

## References

- [Spring Blog: Access API Moves to Spring Security Access](https://spring.io/blog/2025/09/09/access-api-moves-to-spring-security-access)
- Master list entry: 1.62
