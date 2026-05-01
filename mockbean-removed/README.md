# MockBean Removed

@MockBean and @SpyBean annotations removed; use @MockitoBean and @MockitoSpyBean instead.

## What Breaks

Spring Boot 4.0 removed the `@MockBean` and `@SpyBean` annotations from `org.springframework.boot.test.mock.mockito`. Code compiles on Spring Boot 3.5.14 but fails at runtime on 4.0 because the annotation classes no longer exist. The annotations have been replaced with Mockito's native bean override mechanism: `@MockitoBean` and `@MockitoSpyBean` in the `org.springframework.test.context.bean.override.mockito` package.

**Compilation Error on Spring Boot 4.0:**
```
error: cannot find symbol: @MockBean
error: cannot find symbol: @SpyBean
```

## How This Test Works

The test demonstrates the use of both `@MockBean` (full mock) and `@SpyBean` (partial mock/spy) in a Spring Boot test:

- **ServiceTest.testMockBean()**: Uses `@MockBean` to completely replace MyService with a mock. The test stubs behavior using `when()` and verifies method calls with `verify()`.
- **ServiceTest.testSpyBean()**: Uses `@SpyBean` on HelperService to wrap the real bean. Real methods execute unless explicitly stubbed; `verify()` confirms the real methods were called.

The supporting beans (MyService, HelperService) are simple Spring-managed services. The test runs in a `@SpringBootTest` context, which on 3.5.14 correctly injects the mocked/spied beans; on 4.0, the annotations are not recognized.

## On Spring Boot 3.5.14

```bash
mvn clean test
```

Output:
```
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

Both tests pass because `@MockBean` and `@SpyBean` are available in `spring-boot-test`.

## On Spring Boot 4.0

Compilation fails immediately. If you comment out the annotations and try to run, the test fails at runtime with:
```
NoSuchBeanDefinitionException or NullPointerException when accessing the bean
```

Because the annotations are gone and beans are not mocked/spied.

## Fix / Migration Path

Replace all occurrences of the old annotations with their new equivalents:

**1. Update imports:**
```java
// Remove these
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

// Add these
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
```

**2. Replace annotations:**
```java
// @MockBean → @MockitoBean
@MockitoBean
private MyService mockService;

// @SpyBean → @MockitoSpyBean
@MockitoSpyBean
private HelperService spyHelper;
```

**3. Verify pom.xml:**
Ensure `spring-boot-starter-test` is included (brings Mockito and Spring test context bean override support). No additional dependencies required.

**4. Test behavior:**
Run `mvn clean test` to confirm mocks and spies work as expected. The Mockito-native approach may be stricter about stub matching; if needed, use `Mockito.lenient()` to relax matching rules.

## References

- Spring Boot 4.0 Migration Guide (Testing section): https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.0 Bean Override: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
- Mockito Documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
