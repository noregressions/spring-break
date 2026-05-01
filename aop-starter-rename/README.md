# AOP Starter Renamed (Tier 1: Won't Compile)

**Summary**: `spring-boot-starter-aop` has been removed from the Spring Boot BOM in version 4.0. It has been replaced by `spring-boot-starter-aspectj` to better reflect its primary use case and modularization.

## What breaks

In Spring Boot 3.5 and earlier, you could include AOP support by adding the following dependency without a version:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

In Spring Boot 4.0, this artifact is no longer managed by the BOM. Attempting to use it without an explicit version will result in a Maven "version is missing" error.

## How this test works

The `pom.xml` for this module declares `spring-boot-starter-aop` without a version.

On Boot 3.5: The build succeeds as the version is provided by the BOM.
On Boot 4.0: The build fails at the `validate` phase with a missing version error.

## Fix / Migration Path

Replace the dependency with `spring-boot-starter-aspectj`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aspectj</artifactId>
</dependency>
```

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) — AOP Starter
- Master list entry: 1.10
