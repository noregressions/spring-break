# @EntityScan Relocated (Tier 1: Won't Compile)

**Summary**: In Spring Boot 4.0, the `@EntityScan` annotation has been moved from the `org.springframework.boot.autoconfigure.domain` package to the new `org.springframework.boot.persistence.autoconfigure` package. This change is part of a broader effort to modularise persistence-related code into a separate `spring-boot-persistence` module.

## What breaks

On Spring Boot 3.5, `@EntityScan` is located in `org.springframework.boot.autoconfigure.domain`. Code importing it from the old location will fail to compile on 4.0.

```java
import org.springframework.boot.autoconfigure.domain.EntityScan; // Fails on 4.0
```

## How this test works

The module contains:
- `EntityScanApp.java`: A Spring Boot application using `@EntityScan` with the legacy import path.
- `EntityScanTest.java`: A test that attempts to load the application context and also checks for the class existence via reflection.

On Boot 3.5: The application compiles and the test passes.
On Boot 4.0: Compilation fails because the package `org.springframework.boot.autoconfigure.domain` no longer contains `EntityScan`.

## Fix / Migration Path

Update the import statement to use the new package:

```java
import org.springframework.boot.persistence.autoconfigure.EntityScan;
```

## References

- [Spring Boot 4.0.0 M3 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0.0-M3-Release-Notes) — Persistence Modules
- Master list entry: 1.47
