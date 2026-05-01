# Testcontainers postgresql artifact renamed (Tier 1: Won't Resolve)

**Summary**: Spring Boot 4.0 brings in Testcontainers 2.0, which renames `org.testcontainers:postgresql` to `org.testcontainers:testcontainers-postgresql`. Pom files that declare the old artifact without an explicit version fail at Maven resolution. Class packages have also moved as a downstream consequence.

## What breaks

In Boot 3.5 the BOM brings in Testcontainers 1.x, which exposes per-database modules at `org.testcontainers:postgresql`, `org.testcontainers:mysql`, etc. Boot 4.0's BOM brings in Testcontainers 2.x, where those artifacts are renamed to `testcontainers-postgresql`, `testcontainers-mysql`, etc. The old artifact IDs no longer exist.

Existing pom files that depend on the old IDs (without a version, relying on the BOM) fail at Maven resolution before any compilation:

```
'dependencies.dependency.version' for org.testcontainers:postgresql:jar is missing
The build could not read 1 project
```

## How this test works

`ContainerRelocationTest` uses `Class.forName(...)` to check that `org.testcontainers.containers.PostgreSQLContainer` is on the classpath.

On Boot 3.5: Testcontainers 1.x resolves; the class is available at the historic package; the test passes.

On Boot 4.0: the pom never resolves. The test never compiles, never runs.

## Fix / Migration Path

Update the pom to use the new Testcontainers 2.x artifact IDs:

```xml
<!-- Old (Testcontainers 1.x) -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>

<!-- New (Testcontainers 2.x) -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

After the artifact rename, you'll hit the *next* breakage: Testcontainers 2.0 also relocated the container classes from the monolithic `org.testcontainers.containers` package into per-database packages such as `org.testcontainers.postgresql`. Existing imports like:

```java
import org.testcontainers.containers.PostgreSQLContainer;
```

become:

```java
import org.testcontainers.postgresql.PostgreSQLContainer;
```

This module's test reflects the import-level breakage by looking for the old class name. After both fixes (artifact rename and import update) the test would need to be rewritten to look for the class at its new location.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Testcontainers releases](https://github.com/testcontainers/testcontainers-java/releases)
