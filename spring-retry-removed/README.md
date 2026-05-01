# Spring Retry Removed from BOM (Tier 1: Won't Launch)

**Summary**: Spring Boot 4.0 removes `spring-retry` from the BOM. Builds fail at dependency resolution time if spring-retry is declared without an explicit version.

## What Breaks

Spring Boot 4.0 **removed** `spring-retry` from the dependency management BOM. Retry support was moved into Spring Framework 7 core. This is a **Tier 1 failure**: the build fails at dependency resolution before compilation even starts.

1. **BOM removal**: `spring-retry` no longer managed by Spring Boot 4.0 BOM
2. **Version property missing**: `spring-retry.version` property no longer exists in BOM
3. **Unversioned declarations fail**: Any `pom.xml` with `<dependency><artifactId>spring-retry</artifactId></dependency>` (no version) fails to resolve
4. **Build fails early**: Error at Maven's dependency resolution phase, before compilation

## How This Test Works

The test module demonstrates the spring-retry BOM removal:

- **SpringRetryRemovedTest.java**: Two tests using the `@Retryable` annotation and reflection:
  - `springRetryAnnotationIsAvailable()` — uses the annotation directly
  - `retryableCanBeLoadedByReflection()` — uses reflection to load the class
- **App.java**: Minimal Spring Boot application
- **pom.xml**: Declares spring-retry without explicit version (relies on BOM management)

## On Spring Boot 3.5.14

```bash
mvn clean verify
```

**Result**: ✓ Build succeeds. spring-retry 2.0.x is downloaded via BOM management.

```
[INFO] Downloading spring-retry:2.0.x
[INFO] Tests run: 2, Failures: 0, Errors: 0
[INFO] BUILD SUCCESS
```

Both tests pass; the `@Retryable` annotation is on the classpath.

## On Spring Boot 4.0

```bash
mvn clean verify
```

**Result**: ✗ Build fails at dependency resolution phase.

```
[ERROR] Failed to execute goal on project spring-retry-removed:
Could not resolve dependencies for project:
Could not find artifact org.springframework.retry:spring-retry:jar:(null)
in central repository
```

## Fix / Migration Path

### 1. Option: Add Explicit Version to pom.xml

If your code requires spring-retry, declare it with an explicit version (not recommended for long-term maintenance):

```xml
<dependency>
  <groupId>org.springframework.retry</groupId>
  <artifactId>spring-retry</artifactId>
  <version>2.0.5</version>  <!-- Explicit version required on Boot 4.0 -->
</dependency>
```

Note: Spring Retry 2.0.x is no longer actively maintained.

### 2. Recommended: Remove spring-retry Dependency

Most retry functionality is now integrated into Spring Framework 7. If you only use `@Retryable` annotation:

```xml
<!-- REMOVE THIS from pom.xml -->
<dependency>
  <groupId>org.springframework.retry</groupId>
  <artifactId>spring-retry</artifactId>
</dependency>
```

Spring Framework 7 provides native retry support without the separate library.

### 3. Migrate to Spring Framework 7 Retry APIs

Spring Framework 7 includes retry support as part of the core library:

```java
// The @Retryable annotation is available in Spring Framework 7
import org.springframework.retry.annotation.Retryable;

@Component
public class DataFetcher {
    @Retryable(maxAttempts = 3)
    public String fetchData(String id) {
        return externalService.getData(id);
    }
}
```

### 4. Find All spring-retry Usage

Search your codebase for spring-retry dependencies and usage:

```bash
grep -r "spring.retry" pom.xml
grep -r "RetryTemplate\|@Retryable\|@Recover" --include="*.java" src/
```

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Framework 7 What's New](https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-7.x)
- [Spring Retry GitHub](https://github.com/spring-projects/spring-retry)
```

**Expected Output**:
```
[ERROR] COMPILATION FAILURE
[ERROR] Unknown version for artifact org.springframework.retry:spring-retry
[ERROR] The dependency: org.springframework.retry:spring-retry:jar:RELEASE
[ERROR] is missing a version specification. 
[ERROR] This version must be declared explicitly on Boot 4.0, since it was removed from the BOM.
[INFO] BUILD FAILURE
```

The build fails at dependency resolution — Maven cannot find a version for spring-retry because it's no longer in the BOM.

## What Breaks in Real Code

### Scenario 1: Simple @Retryable Usage

```java
// src/main/java
@Component
public class DataService {
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String fetchData(String id) {
        // Unreliable external API call
        return externalApi.getData(id);
    }

    @Recover
    public String fallback(Exception e, String id) {
        return "default-" + id;
    }
}
```

**Reactor 3.x**: Works fine. spring-retry is available via BOM.
**Reactor 4.0**: Build fails at dependency resolution. Cannot find spring-retry version.

### Scenario 2: RetryTemplate Usage

```java
@Configuration
public class RetryConfig {
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        ExponentialBackOffPolicy backoff = new ExponentialBackOffPolicy();
        backoff.setInitialInterval(1000);
        template.setBackOffPolicy(backoff);
        return template;
    }
}
```

**Boot 3.x**: Works. spring-retry classes are available.
**Boot 4.0**: Dependency resolution fails before the code is even compiled.

## The Fix

Spring Boot 4.0 moved retry support into Spring Framework 7 core. You have two options:

### Option 1: Use Spring Framework 7 Core Retry (Recommended)

Spring Framework 7.0+ includes `RetryTemplate` and retry support natively:

```java
// No import from spring-retry needed
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        // Configure with Spring Framework 7 APIs
        return template;
    }
}
```

**pom.xml**: No spring-retry dependency needed. It's built into spring-framework.

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-framework-bom</artifactId>
    <version>7.0.x</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

### Option 2: Explicitly Declare spring-retry (Legacy)

If you must use spring-retry 2.0.x on Boot 4.0, add an explicit version:

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
    <version>2.0.7</version>  <!-- Explicit version required -->
</dependency>
```

This is not recommended, as spring-retry is no longer actively maintained.

### Option 3: Migrate to Spring Framework 7 Declarative Retry

Use the `@Retryable` annotation from Spring Framework directly (if available):

```java
import org.springframework.retry.annotation.Retryable;  // New location in Spring Framework 7

@Retryable(maxAttempts = 3)
public String fetchData(String id) {
    return externalApi.getData(id);
}
```

## Migration Checklist

- [ ] Identify all `spring-retry` usage in pom.xml and parent POMs
- [ ] Check for `@Retryable` and `@Recover` annotations in code
- [ ] Search for `RetryTemplate` and `RetryOperationsInterceptor` usage
- [ ] Review Spring Framework 7 retry documentation for equivalent APIs
- [ ] Remove spring-retry from pom.xml
- [ ] Update imports to use Spring Framework 7 retry classes
- [ ] Test retry logic with new Spring Framework implementation
- [ ] Verify @Retryable annotation still works or update to Spring Framework 7 version
- [ ] Run full integration test suite to verify retry behaviour

## Key Differences: spring-retry vs Spring Framework 7

| Feature | spring-retry 2.0 | Spring Framework 7 |
|---------|------------------|--------------------|
| Dependency | Explicit | Built-in (spring-core, spring-beans) |
| `@Retryable` | org.springframework.retry.annotation | org.springframework.retry.annotation |
| `RetryTemplate` | org.springframework.retry.support | org.springframework.retry.support |
| Backoff Policies | ExponentialBackOffPolicy, LinearBackOffPolicy | Similar classes in spring-core |
| Active Development | No longer maintained | Actively maintained as part of Spring Framework |

