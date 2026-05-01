# AntPathRequestMatcher Removed (Tier 1: Won't Compile)

**Summary**: Spring Security 7.0 removes `AntPathRequestMatcher` in favour of `PathPatternRequestMatcher`. Code that imports the old class fails to compile on Spring Boot 4.0.

## What breaks

Spring Security 6.x exposed two request-matcher implementations side by side:

- `org.springframework.security.web.util.matcher.AntPathRequestMatcher` — the long-standing matcher backed by `AntPathMatcher`.
- `org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher` — the newer matcher backed by `PathPatternParser`.

Spring Security 7.0 (the version Boot 4.0 brings in) **removes** `AntPathRequestMatcher` entirely. Any code that imports it — whether in production filter chains or in tests using `SecurityMockMvcRequestMatchers` — fails at compile time:

```
package org.springframework.security.web.util.matcher does not exist
cannot find symbol: class AntPathRequestMatcher
```

## How this test works

`SecurityConfig` deliberately imports `AntPathRequestMatcher` and uses it to build a request matcher in the filter chain. `PathMatchingEngineTest` instantiates it directly and verifies the class is on the classpath.

On Boot 3.5 (Spring Security 6.5): imports resolve, both tests pass.

On Boot 4.0 (Spring Security 7.0): the import in `SecurityConfig` and `PathMatchingEngineTest` fails. The build never reaches the test phase.

## Fix / Migration Path

Replace `AntPathRequestMatcher` with `PathPatternRequestMatcher`. The new class lives in `org.springframework.security.web.servlet.util.matcher` and uses a `Builder` pattern:

```java
// Before (Security 6.x)
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

RequestMatcher matcher = new AntPathRequestMatcher("/public/**");
```

```java
// After (Security 7.0)
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

RequestMatcher matcher = PathPatternRequestMatcher.withDefaults().matcher("/public/**");
```

Note that `PathPatternRequestMatcher`'s pattern syntax differs from `AntPathRequestMatcher` in one important respect: `**` is only allowed at the *end* of a pattern. Patterns like `/api/**/admin` (with `**` in the middle) that worked under the Ant matcher are rejected by the path-pattern parser.

## References

- [Spring Security 7 Migration Guide](https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [PathPatternRequestMatcher javadoc (Spring Security 7)](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/servlet/util/matcher/PathPatternRequestMatcher.html)
