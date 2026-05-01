# MockitoTestExecutionListener Removed (Tier 2: Won't Run)

**Summary**: In Spring Boot 4.0, `MockitoTestExecutionListener` has been removed. In previous versions, this listener was automatically registered and would handle the initialization of `@Mock` and `@Captor` fields in Spring-bootstrapped tests. In 4.0, you must explicitly use `@ExtendWith(MockitoExtension.class)` or use the new Spring Framework 7.0 `@MockitoBean` / `@MockitoSpyBean` annotations.

## What breaks

Tests that rely on Spring Boot to automatically initialize `@Mock` fields without an explicit Mockito extension will find those fields are `null` at runtime in 4.0.

```java
@SpringBootTest
class MyTest {
    @Mock
    private MyService myService; // Null in 4.0 unless extension added
}
```

## How this test works

The module contains:
- `MyService.java`: A simple service interface to be mocked.
- `MockitoListenerTest.java`: A `@SpringBootTest` that uses `@Mock` without `@ExtendWith(MockitoExtension.class)`.

On Boot 3.5: The test passes because `MockitoTestExecutionListener` is active.
On Boot 4.0: The test fails with a `NullPointerException` (or assertion failure) because the `@Mock` field is never initialized.

## Fix / Migration Path

Option 1: Explicitly add the Mockito Extension:
```java
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private MyService myService;
}
```

Option 2: Migrate to the new Spring Framework 7.0 `@MockitoBean` (which also replaces `@MockBean`):
```java
@SpringBootTest
class MyTest {
    @MockitoBean
    private MyService myService;
}
```

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Master list entry: 1.39
