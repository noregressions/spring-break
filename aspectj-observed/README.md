# spring-boot-starter-aop dropped from BOM (Tier 1: Won't Resolve)

**Summary**: Spring Boot 4.0 drops the `spring-boot-starter-aop` artifact. Pom files that declare it without an explicit version fail at Maven dependency resolution before any compilation happens.

## What breaks

In Spring Boot 3.5 the BOM manages `spring-boot-starter-aop`. Depending on it (without a version) pulls in `spring-aop`, `aspectjweaver`, and `spring-boot-starter` — everything an application needs for AOP-driven annotations like `@Observed`, `@Async`, `@Cacheable`, `@Retryable`, or custom `@Aspect` classes.

Spring Boot 4.0 removes the starter from the BOM entirely. There is no `spring-boot-starter-aop:4.0.x` artifact. Existing pom files declaring it fail at Maven resolution:

```
'dependencies.dependency.version' for org.springframework.boot:spring-boot-starter-aop:jar is missing
The build could not read 1 project
```

## How this test works

`AspectJObservedTest` exercises the `@Observed` annotation via Micrometer's `TestObservationRegistry`.

On Boot 3.5: `spring-boot-starter-aop` resolves, the AOP autoconfiguration activates, the observation is recorded, the test passes.

On Boot 4.0: the pom never resolves. The test never compiles, never runs. The script reports a Tier 1 failure.

## Fix / Migration Path

Depend on `spring-aop` and `aspectjweaver` directly. Both are BOM-managed in Spring Boot 4.0:

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

After this change the build resolves on both 3.5 and 4.0, and `@Observed` continues to record observations. `AopAutoConfiguration` is still present in `spring-boot-autoconfigure` 4.0.x — the only thing that's gone is the convenience starter pom.

## Downstream consequence

If a migrating user adds an explicit `<version>` to keep `spring-boot-starter-aop` working on 4.0 (rather than switching to the underlying artifacts), they pin themselves to a 3.5-era jar in a 4.0 project, which is a separate problem.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Micrometer Observation documentation](https://docs.micrometer.io/micrometer/reference/observation.html)
