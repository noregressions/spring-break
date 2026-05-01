# Actuator @Nullable Removed (Tier 1: Won't Compile)

**Summary**: Support for `org.springframework.lang.Nullable` on Actuator endpoint parameters has been removed in Spring Boot 4.0. It has been replaced by `org.jspecify.annotations.Nullable`.

## What breaks

In Spring Boot 3.5, you could use `org.springframework.lang.Nullable` to indicate that an Actuator endpoint parameter is optional.

In Spring Boot 4.0, support for this annotation (along with `javax.annotation.Nonnull`) was removed. While `org.springframework.lang.Nullable` might still exist in the framework for internal use, it is deprecated and no longer supported for endpoint parameter binding. Most importantly, Spring Boot 4.0 has transitioned to the JSpecify model.

```java
// Spring Boot 3.5 (Works)
import org.springframework.lang.Nullable;

@Endpoint(id = "my-endpoint")
public class MyEndpoint {
    @ReadOperation
    public String get(@Nullable String name) {
        return "Hello " + name;
    }
}

// Spring Boot 4.0 (Compilation Error / Binding Failure)
// In some cases, the annotation itself may be missing or relocated, 
// but primarily it's no longer recognized by the Actuator binding logic.
```

## How this test works

The module `actuator-nullable-removed` contains:
- `NullableEndpoint.java`: An Actuator endpoint using `@org.springframework.lang.Nullable`.
- `ActuatorNullableTest.java`: A test verifying the setup.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Demonstrates the removal of support for the legacy nullability annotation.

## Fix / Migration Path

Migrate to `org.jspecify.annotations.Nullable`.

```java
// Spring Boot 4.0 (Fixed)
import org.jspecify.annotations.Nullable;

@Endpoint(id = "my-endpoint")
public class MyEndpoint {
    @ReadOperation
    public String get(@Nullable String name) {
        return "Hello " + name;
    }
}
```

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Null-safe applications with Spring Boot 4](https://spring.io/blog/2025/11/12/null-safe-applications-with-spring-boot-4)
- Master list entry: 1.70
