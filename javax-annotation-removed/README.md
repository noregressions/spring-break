# javax.annotation and javax.inject Removed (Tier 1: Won't Compile/Won't Run)

**Summary**: Spring Framework 7.0 (included in Spring Boot 4.0) has officially removed support for the legacy `javax.annotation` (e.g., `@PostConstruct`, `@PreDestroy`, `@Resource`) and `javax.inject` (e.g., `@Inject`, `@Named`) annotations. These have been replaced by their `jakarta.*` equivalents as part of the move to Jakarta EE 11.

## What breaks

1. **Injection**: Fields or methods annotated with `javax.inject.Inject` will no longer be automatically injected by Spring.
2. **Lifecycle**: Methods annotated with `javax.annotation.PostConstruct` or `javax.annotation.PreDestroy` will no longer be called by the Spring container.
3. **Resource Lookup**: `@javax.annotation.Resource` will no longer trigger bean lookup.

Note: While the classes might still be on the classpath (if explicitly added as dependencies), Spring no longer recognizes them as valid markers for injection or lifecycle management.

## How this test works

The module contains:
- `JavaxLifecycleService.java`: A Spring component using `javax.annotation.PostConstruct` and `javax.inject.Inject`.
- `JavaxAnnotationTest.java`: A `@SpringBootTest` that verifies if injection happened and if the post-construct method was called.

On Boot 3.5: The test passes as Spring still supports these legacy annotations.
On Boot 4.0: The test fails because Spring ignores the `javax` annotations. The `@Inject` field remains `null` and the `@PostConstruct` method is never executed.

## Fix / Migration Path

Switch all `javax.*` annotations to their `jakarta.*` counterparts:

- `javax.inject.Inject` -> `jakarta.inject.Inject`
- `javax.annotation.PostConstruct` -> `jakarta.annotation.PostConstruct`
- `javax.annotation.PreDestroy` -> `jakarta.annotation.PreDestroy`
- `javax.annotation.Resource` -> `jakarta.annotation.Resource`

## References

- [Spring Framework 7.0 Release Notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes)
- Master list entry: 1.30
