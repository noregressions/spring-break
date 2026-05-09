# Tracing Auto-Configuration Relocated to its Own Module (Tier 3: Different Results)

**Summary**: Spring Boot 4.0 split the monolithic `spring-boot-actuator-autoconfigure` into per-concern modules. The tracing auto-configuration moved to a new artifact, `spring-boot-micrometer-tracing-brave`, which `spring-boot-starter-actuator` does NOT pull in transitively. Boot 3.x apps that relied on `actuator + micrometer-tracing-bridge-brave` to wire tracing get **silent loss of tracing** on Boot 4 unless they add the new module explicitly.

## What Breaks

The class moved on two axes simultaneously — package and jar:

| | Boot 3.5.14 | Boot 4.0.6 |
|---|---|---|
| Class FQN | `org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration` | `org.springframework.boot.micrometer.tracing.brave.autoconfigure.BraveAutoConfiguration` |
| Containing jar | `spring-boot-actuator-autoconfigure` | `spring-boot-micrometer-tracing-brave` (NEW) |
| Pulled in by `spring-boot-starter-actuator`? | Yes (transitively) | **No** |

So the same dependency set that wired tracing on Boot 3 silently produces a tracing-free application on Boot 4. No startup error, no warning, no log line — just no traces in your observability backend.

The same pattern affects other actuator concerns. `spring-boot-actuator-autoconfigure-4.0.6` has been dramatically slimmed; new sibling modules include:

- `spring-boot-micrometer-metrics`
- `spring-boot-micrometer-observation`
- `spring-boot-micrometer-tracing-brave`
- `spring-boot-micrometer-tracing-opentelemetry`
- `spring-boot-health`
- `spring-boot-mongodb` (host of the relocated MongoDB health indicators)

This module focuses on the tracing case as the most user-visible instance; the broader pattern is that each concern now has its own auto-configure jar that must be added explicitly.

## How This Test Works

The pom declares the Boot-3-era dependency set: `spring-boot-starter-actuator` plus `micrometer-tracing-bridge-brave`. Nothing more. The test asserts that `BraveAutoConfiguration` is reachable on the classpath via `Class.forName(...)` — checking both the Boot-3 package and the Boot-4 package. If neither resolves, tracing is silently not wired.

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Test passes. The Boot-3 `BraveAutoConfiguration` class is on the classpath via `spring-boot-actuator-autoconfigure`.

## On Spring Boot 4.0.6

```bash
mvn test -Dspring-boot.version=4.0.6
```

**Result**: ✗ Test fails.

**Error**:
```
BraveAutoConfiguration should be reachable on the classpath. ...
Found: oldPath=false newPath=false
```

Neither the old (`actuate.autoconfigure.tracing`) nor the new (`micrometer.tracing.brave.autoconfigure`) `BraveAutoConfiguration` is on the classpath because neither is pulled in by `spring-boot-starter-actuator` on Boot 4 alone.

## Fix / Migration Path

Add the new module as an explicit dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-micrometer-tracing-brave</artifactId>
</dependency>
```

For OpenTelemetry users, the equivalent is `spring-boot-micrometer-tracing-opentelemetry`. Both are managed by the Boot 4 BOM (no version needed).

Empirically verified: with `spring-boot-micrometer-tracing-brave:4.0.6` added, the new-package `BraveAutoConfiguration` is on the classpath and tracing auto-configures normally.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Sibling module: `auto-configure-observability-removed` (the test annotation that was removed alongside this restructure)
