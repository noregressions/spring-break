# spring-boot-starter-aop dropped, breaking @Retryable + @Transactional wiring (Tier 1: Won't Resolve)

**Summary**: Spring Boot 4.0 drops the `spring-boot-starter-aop` artifact. Modules that combine `@Retryable` and `@Transactional` (where AOP advice ordering matters) fail at Maven resolution.

## What breaks

This module's test asserts that a failed retry attempt is rolled back within an enclosing transaction — which relies on the relative ordering of the retry advice and the transaction advice in Spring's AOP chain. To run that test at all, AOP needs to be wired up, and on Boot 3.5 that's done by depending on `spring-boot-starter-aop`.

Spring Boot 4.0 removes the starter from the BOM. Existing pom files declaring it fail at Maven resolution:

```
'dependencies.dependency.version' for org.springframework.boot:spring-boot-starter-aop:jar is missing
The build could not read 1 project
```

## How this test works

`RetryableTransactionOrderTest` calls a method annotated with both `@Retryable` and `@Transactional` that fails twice and succeeds on the third attempt, writing an `AuditLog` row each time. The test asserts exactly one row exists at the end — meaning the failed attempts were rolled back within the same outer transaction.

On Boot 3.5: `spring-boot-starter-aop` resolves, both advices are wired up in the historic order, the test passes.

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

After this, the build resolves on both 3.5 and 4.0, and the test continues to pass — provided you stay on Spring Retry 2.x.

## Downstream consequence: advice ordering on Spring Retry 3.x

A user upgrading to Spring Retry 3.x in the same migration will hit a separate change: the advice order between `@Retryable` and `@Transactional` flipped, so that retry now wraps *outside* the transaction. Each retry gets its own transaction, and failed attempts commit their `AuditLog` row instead of being rolled back. The same test would then see three `AuditLog` rows instead of one.

This module does not currently exercise that downstream change, because it pins `spring-retry` to 2.0.10. If you want the full demo, pin `spring-retry` to a 3.x version and re-run.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Retry releases](https://github.com/spring-projects/spring-retry/releases)
