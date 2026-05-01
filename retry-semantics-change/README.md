# spring-boot-starter-aop dropped, breaking @Retryable wiring (Tier 1: Won't Resolve)

**Summary**: Spring Boot 4.0 drops the `spring-boot-starter-aop` artifact. Modules that depend on it for `@Retryable` proxy creation — including any module using Spring Retry — fail at Maven resolution.

## What breaks

Spring Retry's `@Retryable` annotation is implemented as a Spring AOP advice. In Spring Boot 3.5, the convention is to depend on `spring-boot-starter-aop` so the necessary proxy machinery (`spring-aop`, `aspectjweaver`) is on the classpath.

Spring Boot 4.0 removes that starter from the BOM. Existing pom files declaring it fail at Maven resolution:

```
'dependencies.dependency.version' for org.springframework.boot:spring-boot-starter-aop:jar is missing
The build could not read 1 project
```

## How this test works

`RetryServiceTest` calls a method annotated with `@Retryable(maxAttempts = 3)` that always throws, then asserts the method was invoked exactly 3 times.

On Boot 3.5: `spring-boot-starter-aop` resolves, AOP proxies are created, the retry advice fires, three invocations are recorded, the test passes.

On Boot 4.0: the pom never resolves. The test never runs. The script reports a Tier 1 failure.

## Fix / Migration Path

Replace `spring-boot-starter-aop` with explicit `spring-aop` + `aspectjweaver` (both BOM-managed on 3.5 and 4.0):

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```

After this, the build resolves on both 3.5 and 4.0, and `@Retryable` continues to work as before — provided you stay on Spring Retry 2.x. This module pins `spring-retry` to 2.0.10 explicitly because Spring Retry is not in the Boot BOM at all in either version.

## Downstream consequence: maxAttempts → maxRetries

A user fixing the AOP issue and *also* upgrading to Spring Retry 3.x will hit a separate semantic change: in Spring Retry 3.x, `@Retryable(maxAttempts = 3)` was renamed to `@Retryable(maxRetries = 3)` and the count interpretation flipped from "total invocations" to "retries after the initial attempt". A method previously called 3 times will now be called 4 times.

This module does not currently exercise that downstream change, because it pins `spring-retry` to 2.0.10. If you want a demo that triggers the count change as well as the AOP issue, pin `spring-retry` to a 3.x version and update the pom and READMEs accordingly.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Retry releases](https://github.com/spring-projects/spring-retry/releases)
