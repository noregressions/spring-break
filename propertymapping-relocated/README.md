# @PropertyMapping Relocated (Tier 1: Won't Compile)

**Summary**: In Spring Boot 4.0, the `@PropertyMapping` annotation has been moved from `org.springframework.boot.test.autoconfigure.properties` to `org.springframework.boot.test.context`. This annotation is typically used when creating custom test slices or auto-configurations for tests.

## What breaks

On Spring Boot 3.5, `@PropertyMapping` is located in `org.springframework.boot.test.autoconfigure.properties`. Code importing it from this location will fail to compile on 4.0.

```java
import org.springframework.boot.test.autoconfigure.properties.PropertyMapping; // Fails on 4.0
```

## How this test works

The module contains:
- `CustomTestAnnotation.java`: A custom annotation that uses `@PropertyMapping`.
- `PropertyMappingTest.java`: A test verifying the existence of the annotation.

On Boot 3.5: The code compiles and the test passes.
On Boot 4.0: Compilation fails because the package `org.springframework.boot.test.autoconfigure.properties` no longer exists or no longer contains `@PropertyMapping`.

## Fix / Migration Path

Update the import statement to use the new package:

```java
import org.springframework.boot.test.context.PropertyMapping;
```

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Master list entry: 1.43
