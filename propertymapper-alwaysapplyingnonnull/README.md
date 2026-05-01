# PropertyMapper.alwaysApplyingWhenNonNull Removed (Tier 1: Won't Compile)

**Summary**: `PropertyMapper.alwaysApplyingWhenNonNull()` has been removed in Spring Boot 4.0 because the "when non-null" behavior is now the default.

## What breaks

Code that calls `alwaysApplyingWhenNonNull()` on a `PropertyMapper` instance will fail to compile on Spring Boot 4.0.

```java
PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
```

## How this test works

The module includes a class `PropertyMapperUsage` that calls the removed method.

On Boot 3.5: Compiles and runs.
On Boot 4.0: Fails to compile because the method no longer exists.

## Fix / Migration Path

Simply remove the call to `alwaysApplyingWhenNonNull()`. If you need to map `null` values explicitly (which was what this method was used to *avoid*), use the new `always()` method on individual mappings.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Master list entry: 1.14
