# JUnit 4 Vintage Engine Removed (Tier 1: Won't Launch)

**Summary**: Spring Boot 4.0 removes junit-vintage-engine, breaking any code that relies on JUnit 4 APIs (`@RunWith`, `@Before`, `@Rule`, `org.junit.Assert`).

## What Breaks

Spring Boot 4.0 drops the `junit-vintage-engine` dependency that provided backward compatibility with JUnit 4. This breaks all code using JUnit 4 patterns:

1. **JUnit 4 Vintage Engine removed** — `junit-vintage-engine` no longer exists in Spring Boot 4.0 BOM
2. **JUnit 4 APIs unavailable** — `@RunWith`, `@Before`, `@After`, `@Rule`, `org.junit.Assert` all missing
3. **Test runner classes gone** — `SpringRunner`, `BlockJUnit4ClassRunner` no longer available
4. **Build fails at test runtime** — Tests that import `org.junit.*` fail to compile or run
5. **Only JUnit Jupiter (JUnit 5+) supported** — JUnit 6 and later with JUnit Platform 2.x

This is a **Tier 1 failure**: tests won't compile or run on Boot 4.0.

## How This Test Works

The test module contains a simple JUnit 5 test suite that verifies whether JUnit 4 API classes are available via the vintage engine:

- **LegacyTest.java**: Uses reflection to check if JUnit 4 classes (`org.junit.RunWith`, `org.junit.Before`, `org.junit.Rule`, `org.springframework.test.context.junit4.SpringRunner`) exist on the classpath
- **App.java**: Minimal Spring Boot application

The test uses `assertDoesNotThrow()` with `Class.forName()` to verify JUnit 4 API availability at runtime.

## On Spring Boot 3.4.1

```bash
mvn clean test
```

**Result**: ✓ All tests pass. The junit-vintage-engine is available and transitively pulls in JUnit 4 APIs.

**Output**:
```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.LegacyTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.4s
```

All reflection checks pass:
- `org.junit.RunWith` found ✓
- `org.junit.Before` found ✓
- `org.junit.After` found ✓
- `org.junit.Rule` found ✓
- `org.springframework.test.context.junit4.SpringRunner` found ✓

## On Spring Boot 4.0

```bash
mvn clean test
```

**Result**: ✗ Tests fail at runtime. JUnit 4 API classes are not on the classpath.

**Output**:
```
[ERROR] Tests run: 4, Failures: 4, Errors: 0, Skipped: 0
[ERROR] org.junit.RunWith not found on classpath
[ERROR] org.junit.Before not found
[ERROR] org.junit.Rule not found
[ERROR] SpringRunner not found
```

## Fix / Migration Path

### 1. Remove junit-vintage-engine from pom.xml

The vintage engine doesn't exist in Boot 4.0; remove any explicit dependency:

```xml
<!-- DELETE THIS -->
<dependency>
  <groupId>org.junit.vintage</groupId>
  <artifactId>junit-vintage-engine</artifactId>
  <scope>test</scope>
</dependency>
```

### 2. Migrate JUnit 4 Tests to JUnit Jupiter

Replace JUnit 4 annotations with JUnit 5 equivalents:

| JUnit 4 | JUnit 5 (Jupiter) |
|---------|-------------------|
| `@RunWith(SpringRunner.class)` | `@SpringBootTest` (no runner needed) |
| `@Before` | `@BeforeEach` |
| `@After` | `@AfterEach` |
| `@BeforeClass` | `@BeforeAll` (must be static) |
| `@AfterClass` | `@AfterAll` (must be static) |
| `@Rule TemporaryFolder` | `@TempDir Path` |
| `@Rule` (custom) | `@ExtendWith(...)` |
| `import org.junit.Test` | `import org.junit.jupiter.api.Test` |
| `org.junit.Assert.assertEquals()` | `org.junit.jupiter.api.Assertions.assertEquals()` |

### 3. Example Migration

```java
// OLD (JUnit 4)
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LegacyTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void setUp() { }
    
    @Test
    public void testSomething() { }
}

// NEW (JUnit 5)
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.file.Path;

@SpringBootTest
public class LegacyTest {
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() { }
    
    @Test
    public void testSomething() { }
}
```

### 4. Handle SpringRunner Deprecation

`SpringRunner` is deprecated in Spring Framework 7 and will be removed. Replace:

```java
// OLD
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test { }

// NEW
@SpringBootTest
public class Test { }
```

Jupiter's `@SpringBootTest` automatically detects and configures Spring context.

## References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [JUnit Platform 2.0](https://github.com/junit-team/junit5/wiki)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Framework 7 What's New](https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-7.x)
