# BootstrapRegistry and EnvironmentPostProcessor Relocated (Tier 1: Won't Compile)

**Summary**: Spring Boot 4.0 relocated several core infrastructure classes to more appropriate packages to improve the internal structure of the framework.

## What breaks

In Spring Boot 3.5, `BootstrapRegistry` and its related classes were located in `org.springframework.boot`. In Spring Boot 4.0, they have been moved to `org.springframework.boot.bootstrap`.

Conversely, `EnvironmentPostProcessor`, which was in `org.springframework.boot.env` in 3.5, has been moved to the root `org.springframework.boot` package in 4.0.

Code that imports these classes using their 3.5 package names will fail to compile on Spring Boot 4.0.

### BootstrapRegistry (Boot 3.5)
```java
import org.springframework.boot.BootstrapRegistry; // Fails on 4.0
```

### EnvironmentPostProcessor (Boot 3.5)
```java
import org.springframework.boot.env.EnvironmentPostProcessor; // Fails on 4.0
```

## How this test works

The module contains:
- `BootstrapRegistryUsage`: Direct usage of `org.springframework.boot.BootstrapRegistry`.
- `EnvironmentPostProcessorUsage`: Direct usage of `org.springframework.boot.env.EnvironmentPostProcessor`.
- `RelocationTest`: Asserts that these classes can be used and loaded via their 3.5 package names.

On Boot 3.5: Build succeeds.
On Boot 4.0: Build fails with `cannot find symbol` or `package does not exist` errors during compilation.

## Fix / Migration Path

Update the imports to use the new package names:

- `org.springframework.boot.BootstrapRegistry` → `org.springframework.boot.bootstrap.BootstrapRegistry`
- `org.springframework.boot.ConfigurableBootstrapContext` → `org.springframework.boot.bootstrap.ConfigurableBootstrapContext`
- `org.springframework.boot.env.EnvironmentPostProcessor` → `org.springframework.boot.EnvironmentPostProcessor`

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) — BootstrapRegistry and EnvironmentPostProcessor package changes
- Master list entry: 1.13
