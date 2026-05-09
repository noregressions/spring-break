# spring-jcl Bridge Removed (Tier 1: Won't Build)

**Summary**: Spring Framework 7 removes the `spring-jcl` bridge module. Any POM that declares `spring-jcl` explicitly fails to resolve on Boot 4.0. Boot 4.0 instead manages `commons-logging` (1.3.x) directly via the BOM, so commons-logging APIs still work — but the artifact name is different.

## What Breaks

Spring Boot 4.0 **removes** `org.springframework:spring-jcl`. The artifact no longer exists in the Boot 4.0 BOM. Any explicit declaration of `spring-jcl` (typical in legacy projects, corporate parent POMs, or hand-written dependency trees) fails at Maven dependency resolution.

1. **Artifact removed**: `org.springframework:spring-jcl` does not exist on Boot 4.0
2. **Replacement managed elsewhere**: Boot 4.0 manages `commons-logging:commons-logging` (1.3.x) via the BOM, pulled in transitively through `spring-core`
3. **No code changes required**: code that uses `org.apache.commons.logging.Log` / `LogFactory` continues to work — only the dependency declaration needs to go

## How This Test Works

This module declares `<dependency>org.springframework:spring-jcl</dependency>` with no version, relying on the Boot BOM to provide it.

- **LoggingService.java**: A simple class that uses `org.apache.commons.logging.Log` to log a message
- **SpringJclRemovedTest.java**: Two tests — one that calls the service, one that asserts `LogFactory` loads via reflection
- **pom.xml**: Declares `spring-jcl` explicitly (which is the project's fault, not Spring's, but mirrors what corporate / legacy projects often do)

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Builds and passes. The Boot BOM resolves `spring-jcl` (it's part of Spring Framework 6.2), `LogFactory` is available, both tests pass.

## On Spring Boot 4.0

```bash
mvn test -Dspring-boot.version=4.0.6
```

**Result**: ✗ Build fails at dependency resolution.

**Error**:
```
[ERROR] Failed to execute goal on project spring-jcl-removed:
Could not resolve dependencies for project com.example:spring-jcl-removed:jar:1.0.0:
Could not find artifact org.springframework:spring-jcl:jar:7.0.x
```

Tests never run. The failure is at resolution time, before compilation.

## Fix / Migration Path

Remove the explicit `spring-jcl` dependency. That's all that's required for the build to go green:

```xml
<!-- DELETE THIS -->
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-jcl</artifactId>
</dependency>
```

Commons Logging is now pulled in transitively via `spring-core`. Code using the `org.apache.commons.logging` API continues to work without modification.

## Scope of Proof

This test demonstrates the **narrow Tier 1 failure**: declaring `spring-jcl` explicitly breaks the 4.0 build. Removing the declaration is sufficient to make it green again.

This test deliberately does **not** demonstrate the broader cleanup that the migration guide also recommends — removing legacy `jcl-over-slf4j` dependencies, or removing legacy `commons-logging` exclusions from corporate parent POMs. That advice is project-specific (it depends on how a given project routes Commons Logging through SLF4J) and is correctly framed as a manual verification step in the cheat-sheet card, not a guaranteed-safe automated change.

A follow-up module exercising the corporate "exclude commons-logging + add jcl-over-slf4j" pattern on Boot 4.0 would let us empirically validate (or refute) the cheat-sheet's cleanup advice. Not done here; tracked as a follow-up.

## References

- [Upgrading to Spring Framework 7.x](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-7.x)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/)
