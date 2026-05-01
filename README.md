# Spring Boot 3.5 → 4.0 Migration Test Cases

57 self-contained Maven modules, each demonstrating a specific breaking change at the 3.5→4.0 boundary. The default build uses Spring Boot 3.5.14 — all tests pass. Override the version to see what breaks.

## Quick start

```bash
# Everything green (Spring Boot 3.5.14)
./run-all-tests.sh

# Watch it break (Spring Boot 4.0.6)
./run-all-tests.sh -v 4.0.6

# Quiet mode (just pass/fail summary)
./run-all-tests.sh -v 4.0.6 -q

# Single module via Maven
mvn test -pl jackson-date-serialisation
mvn test -pl jackson-date-serialisation -Dspring-boot.version=4.0.6
```

## Modules by tier

### Tier 1 — Won't Build — 38 modules

Your build breaks immediately. Loud, obvious, caught before deploy.

| Module | What breaks |
|--------|------------|
| `jackson-group-id` | Maven group ID changes from `com.fasterxml.jackson` to `tools.jackson` |
| `undertow-removed` | `spring-boot-starter-undertow` no longer exists |
| `security-removed-apis` | `authorizeRequests()` and `.and()` removed from Spring Security DSL |
| `deprecated-classes-removed` | APIs deprecated across 3.x lifecycle removed in 4.0 |
| `hibernate-session-delete` | `Session.delete(Object)` removed — must use `Session.remove(Object)` |
| `testcontainers-class-relocation` | `PostgreSQLContainer` moved to `org.testcontainers.postgresql` |
| `spring-retry-removed` | `spring-retry` library removed from Boot 4.0 BOM — retry moved to SF7 core |
| `batch-job-serialisation` | `JobExecutionListenerSupport`, `StepExecutionListenerSupport`, `ChunkListenerSupport` removed in Batch 6 |

### Tier 2 — Won't Run — 15 modules

Compiles fine. Throws runtime exceptions on specific code paths.

| Module | What breaks |
|--------|------------|
| `mockbean-removed` | `@MockBean` / `@SpyBean` annotations compile but `MockitoPostProcessor` removed — fields stay null → NPE |
| `jackson-exception-hierarchy` | `JacksonException` no longer extends `IOException` — catch blocks go dead |
| `hibernate-dialect-removal` | Version-specific dialects (`MySQL8Dialect`, etc.) removed — `ClassNotFoundException` |
| `hibernate-cascade-removal` | `CascadeType.SAVE_UPDATE` removed in Hibernate 7 — must use JPA `PERSIST` + `MERGE` |
| `oauth-password-grant-removed` | OAuth 2.0 Password Grant removed per OAuth 2.1 |
| `batch-schema-change` | Spring Batch 6 renames `BATCH_JOB_SEQ` → `BATCH_JOB_INSTANCE_SEQ` — DDL migration required |
| `pkce-mandatory` | PKCE enforced for confidential OAuth 2.0 clients — older providers reject it |

### Tier 3 — Different Results — 4 modules

Compiles, starts, runs, passes your existing tests. Produces different output.

| Module | What breaks |
|--------|------------|
| `jackson-date-serialisation` | Dates flip from `1699257000000` to `"2023-11-06T05:30:00Z"` |
| `retry-semantics-change` | `maxAttempts=3` now means 3 retries (4 total calls), not 3 total |
| `jackson-locale-format` | `Locale.CHINA` serialises as `zh-CN` instead of `zh_CN` — breaks caching, i18n |
| `hibernate-native-datetime` | Native queries return `java.time.LocalDate` instead of `java.sql.Date` — silent type change |

## How it works

The parent POM imports the Spring Boot BOM as a dependency (not as a parent), which means the version is a property you can override from the command line:

```xml
<properties>
    <spring-boot.version>3.5.14</spring-boot.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```bash
mvn test -Dspring-boot.version=4.0.6
```

This is also how most enterprise projects consume Spring Boot (via BOM, not parent), so the test structure mirrors real-world builds.

## Related

These test cases accompany the *"Won't Launch, Won't Run, Wrong Results"* article series on migrating from Spring Boot 3.5 to 4.0.
