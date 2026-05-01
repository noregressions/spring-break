# SpringExtension Method Scope (Tier 2: Won't Run)

**Summary**: In Spring Framework 7.0 (Spring Boot 4.0), the `SpringExtension` for JUnit Jupiter now uses a test-method scoped `ExtensionContext` by default. Previously, it used a test-class scoped context.

## What breaks

The `SpringExtension` integrates the Spring TestContext Framework into JUnit Jupiter. In Spring Framework 6.x, the `ExtensionContext` provided to callbacks was scoped to the test class.

In Spring Framework 7.0, this has changed to be scoped to the test method. While this enables more consistent dependency injection within `@Nested` test class hierarchies, it may break:
1. Custom `TestExecutionListener` implementations that rely on test-class scoped state.
2. Third-party extensions that assumed the old scoping behavior.

To restore the previous behavior, a new annotation `@SpringExtensionConfig` has been introduced.

```java
// Spring Boot 4.0 (Restore old behavior)
@ExtendWith(SpringExtension.class)
@SpringExtensionConfig(useTestClassScopedExtensionContext = true)
class MyTest {
    // ...
}
```

## How this test works

The module `springextension-method-scope` contains:
- `NestedSpringTest.java`: A test class that checks for the existence of `org.springframework.test.context.junit.jupiter.SpringExtensionConfig`.

On Boot 3.5: The class does not exist, and the test passes.
On Boot 4.0: The class exists (indicating Spring 7.0+), and the test fails with an assertion message explaining the change in default scoping.

## Fix / Migration Path

If your tests (especially those with `@Nested` classes) or custom listeners fail due to state being cleared or initialized incorrectly:
1. Annotate the top-level test class with `@SpringExtensionConfig(useTestClassScopedExtensionContext = true)`.
2. Update custom `TestExecutionListener` implementations. Instead of `testContext.getTestClass()`, use `testContext.getTestInstance().getClass()` if you need the current test class in a nested hierarchy.

## References

- [Spring Framework 7.0 Migration Guide](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Migration-Guide)
- Master list entry: 2.14
