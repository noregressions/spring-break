# SimpDestinationMessageMatcher Removed (Tier 1: Won't Compile)

**Summary**: `SimpDestinationMessageMatcher` has been removed in Spring Security 7.0.

## What breaks

Code that uses `SimpDestinationMessageMatcher` for WebSocket security matching will fail to compile on Spring Boot 4.0.

```java
import org.springframework.security.messaging.util.matcher.SimpDestinationMessageMatcher;

// ...
new SimpDestinationMessageMatcher("/topic/**");
```

## How this test works

The module includes a class `SimpDestUsage` that imports and uses `SimpDestMessageMatcher`.

On Boot 3.5: Compiles and runs.
On Boot 4.0: Fails to compile because the class no longer exists.

## Fix / Migration Path

Spring Security 7.0 moves towards `AuthorizationManager` based security for messaging. You should use `MessageMatcherDelegatingAuthorizationManager` or custom matchers if needed.

## References

- [Spring Security 7.0 Migration Guide](https://docs.spring.io/spring-security/reference/6.5-SNAPSHOT/migration-7/index.html)
- Master list entry: 1.59
