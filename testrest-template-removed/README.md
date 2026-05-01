# TestRestTemplate Relocated (Tier 1: Won't Compile)

**Summary**: In Spring Boot 4.0, `TestRestTemplate` has been moved from `org.springframework.boot.test.web.client` to a new package `org.springframework.boot.resttestclient`. This is part of the broader modularization of Spring Boot's testing infrastructure.

## What breaks

Code that imports `TestRestTemplate` from its legacy 3.5 location will fail to compile on Spring Boot 4.0.

```java
import org.springframework.boot.test.web.client.TestRestTemplate; // Fails in 4.0
```

On 4.0, the package `org.springframework.boot.test.web.client` no longer contains `TestRestTemplate`.

## How this test works

The module contains:
- `TestRestTemplateUsage.java`: Attempts to import and use `TestRestTemplate` from the legacy `org.springframework.boot.test.web.client` package.
- `TestRestTemplateRemovedTest.java`: Asserts that `TestRestTemplate` is present on the classpath using both direct reference (for compile-time check) and reflection (for runtime verification).

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile because the legacy package/class is gone.

## Fix / Migration Path

Update the import statement to the new location:

```java
// From
import org.springframework.boot.test.web.client.TestRestTemplate;

// To
import org.springframework.boot.resttestclient.TestRestTemplate;
```

Additionally, consider migrating to the new `RestTestClient` which provides a more modern and fluent API for REST testing.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Master list entry: 1.40
