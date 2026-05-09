# `@AutoConfigureObservability` Removed (Tier 1: Won't Build)

**Summary**: Spring Boot 4.0 removes `org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability` entirely. Tests that opt into observability with this annotation fail to compile on Boot 4. The annotation isn't relocated to a different module — it's gone, along with the underlying suppression mechanism it was opting out of.

## What Breaks

In Spring Boot 3.x, `spring-boot-test-autoconfigure` ships an `ObservabilityContextCustomizerFactory` that injects a `test` PropertySource into `@SpringBootTest` runs, defaulting `management.tracing.enabled=false` to suppress observability noise during tests. `@AutoConfigureObservability` is the opt-out: applying it to a test class re-enables observability for that test.

In Spring Boot 4.0:
- The annotation is removed from `spring-boot-test-autoconfigure`.
- The `ObservabilityContextCustomizerFactory` mechanism is also removed.
- Tests that imported the annotation fail at `javac`.

Empirically verified by grepping every `spring-boot-*-4.0.6.jar` in the Maven cache — zero matches for `AutoConfigureObservability`.

## How This Test Works

`ObservabilityIntegrationTest` imports the annotation and applies it to a `@SpringBootTest` class:

```java
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;

@SpringBootTest
@AutoConfigureObservability
class ObservabilityIntegrationTest { ... }
```

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Compiles, runs, passes. The annotation is in `spring-boot-test-autoconfigure-3.5.14.jar`.

## On Spring Boot 4.0

```bash
mvn test-compile -Dspring-boot.version=4.0.6
```

**Result**: ✗ Compile fails.

**Error**:
```
package org.springframework.boot.test.autoconfigure.actuate.observability does not exist
cannot find symbol
```

## Fix / Migration Path

**Delete the annotation.** There is no replacement — Boot 4 removed both the annotation and the underlying suppression mechanism. Tests that needed `@AutoConfigureObservability` to opt INTO observability on Boot 3 don't need any opt-in on Boot 4 because the default suppression no longer happens.

```diff
- @SpringBootTest
- @AutoConfigureObservability
- class MyTest { ... }
+ @SpringBootTest
+ class MyTest { ... }
```

Watch for the adjacent concern: tracing auto-configuration itself may not fire on Boot 4 with the old dependency set. See sibling module **tracing-autoconfig-relocated** — `spring-boot-starter-actuator` + `micrometer-tracing-bridge-brave` is no longer enough; you need the new `spring-boot-micrometer-tracing-brave` module added explicitly.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Sibling module: `tracing-autoconfig-relocated`
