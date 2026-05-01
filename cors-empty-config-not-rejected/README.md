# CORS Empty Configuration Not Rejected (Tier 2: Won't Run)

**Summary**: In Spring Framework 7.0 (Spring Boot 4.0), there is a behavioral change in how CORS pre-flight requests are handled when an "empty" `CorsConfiguration` is used.

## What breaks

In Spring Framework 6.x (Spring Boot 3.5), if you register a `CorsConfiguration` that has no allowed origins, methods, or headers (an empty config), Spring's `DefaultCorsProcessor` will reject CORS pre-flight (`OPTIONS`) requests with a `403 Forbidden` status because it fails to match any criteria.

In Spring Framework 7.0, the "CORS Pre-Flight requests behavior change" mentioned in the release notes refers to how these empty or partially configured objects are processed. It may no longer reject them with a 403, or it may apply different defaults. This can lead to unexpected exposure of endpoints or changes in security posture for applications that relied on the "fail-closed" behavior of empty CORS objects.

```java
// Registered via CorsFilter or WebMvcConfigurer
source.registerCorsConfiguration("/**", new CorsConfiguration());
```

## How this test works

The module `cors-empty-config-not-rejected` contains:
- `CorsApp.java`: Configures a `CorsFilter` with an empty `CorsConfiguration`.
- `CorsPreflightTest.java`: Performs an `OPTIONS` request and expects a `403 Forbidden`.

On Boot 3.5: The request is rejected as expected, and the test passes.
On Boot 4.0: The behavior changes (e.g., returns 200 OK or 204 No Content without CORS headers), and the test fails.

## Fix / Migration Path

Explicitly configure your CORS requirements. Do not rely on empty `CorsConfiguration` objects or default-deny behavior that might have shifted. Use `applyPermitDefaultValues()` if you want simple defaults, or explicitly set allowed origins.

```java
// Spring Boot 4.0 (Recommended Fix)
CorsConfiguration config = new CorsConfiguration();
config.addAllowedOrigin("https://example.com");
config.addAllowedMethod("*");
source.registerCorsConfiguration("/**", config);
```

## References

- [Spring Framework 7.0 Release Notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes) — CORS Pre-Flight requests behavior change
- Master list entry: 2.17
