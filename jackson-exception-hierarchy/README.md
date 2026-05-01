# Jackson Exception Hierarchy Changed

JacksonException no longer extends IOException; catch blocks for IOException miss Jackson 3.x parse errors.

## What Breaks

Jackson 3.x changed the exception hierarchy: `JacksonException` now extends `RuntimeException` instead of `IOException`. This is a silent breaking change because code compiles identically on both versions, but catch blocks become dead code.

**Jackson 2.x (Spring Boot 3.5.14):**
```
JacksonException extends IOException
→ catch(IOException e) catches all Jackson parse errors
```

**Jackson 3.x (Spring Boot 4.0):**
```
JacksonException extends RuntimeException
→ catch(IOException e) silently misses Jackson parse errors
```

Existing catch blocks remain syntactically valid but no longer catch Jackson exceptions. Parse errors propagate uncaught, potentially crashing production services.

## How This Test Works

The test uses reflection to load Jackson classes from either package (com.fasterxml.jackson for 2.x, tools.jackson for 3.x). This allows a single test to run on both versions:

- **jacksonExceptionExtendsIOException()**: Verifies whether JacksonException is assignable from IOException using reflection. Passes on 2.x, fails on 3.x.
- **catchIOExceptionPatternStillWorks()**: Attempts to parse malformed JSON and catch the exception as IOException. Succeeds on 2.x (exception caught), fails on 3.x (exception escapes as RuntimeException).
- **validJsonStillParses()**: Confirms valid JSON parses correctly on both versions.

## On Spring Boot 3.5.14

```bash
mvn clean test
```

Output: All tests pass. JacksonException is caught by IOException handlers.

## On Spring Boot 4.0

The first two tests fail:
```
jacksonExceptionExtendsIOException() fails — IOException.isAssignableFrom(JacksonException) is false
catchIOExceptionPatternStillWorks() fails — exception is not caught by catch(IOException)
```

Any application code relying on catch(IOException) to handle Jackson parse errors will miss those errors in production.

## Fix / Migration Path

Update exception handling to explicitly handle Jackson runtime exceptions:

**Option 1: Catch both IOException and JacksonException**
```java
try {
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(jsonString, MyClass.class);
} catch (IOException e) {
    logger.error("IO error: ", e);
} catch (JacksonException e) {
    logger.error("Jackson parsing error: ", e);
}
```

**Option 2: Catch generic Exception**
```java
try {
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(jsonString, MyClass.class);
} catch (Exception e) {
    logger.error("Failed to parse JSON: ", e);
}
```

**Option 3: Use @ControllerAdvice for centralized error handling (REST endpoints)**
```java
@ExceptionHandler(JacksonException.class)
public ResponseEntity<?> handleJacksonException(JacksonException e) {
    return ResponseEntity.badRequest().body("Invalid JSON: " + e.getMessage());
}
```

Import the correct JacksonException:
```java
// Jackson 3.x (Spring Boot 4.0)
import com.fasterxml.jackson.core.JacksonException;
```

## References

- Jackson 3.0 Changes: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
- Jackson 3.0 Release Notes: https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
