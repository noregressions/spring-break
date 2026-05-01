# Jackson Date Serialisation Format Change Migration Test

## One-Line Summary
Jackson 3.0 (Spring Boot 4.0) silently changes default date serialisation from numeric milliseconds to ISO-8601 strings, breaking REST API contracts that expect timestamps.

## What Breaks

Jackson 3.0 bundled with Spring Boot 4.0 changes the default date serialisation behaviour:

**Spring Boot 3.5.14 (Jackson 2.x):** `java.util.Date` → `1699257000000` (numeric millis)
**Spring Boot 4.0 (Jackson 3.x):** `java.util.Date` → `"2023-11-06T05:30:00Z"` (ISO-8601 string)

This is a **silent change with no compilation or runtime errors**. The serialisation format simply changes, breaking any code that depends on:
- REST API clients expecting numeric timestamps
- Contract testing assertions on JSON format
- Mobile/frontend code parsing timestamps as numbers
- Caching layers that use timestamp strings as keys
- Integration tests comparing JSON payloads

## How This Test Works

The test suite uses Java reflection to load Jackson's `ObjectMapper` (handling both Jackson 2.x and 3.x package names) and verifies the date serialisation format via direct JSON inspection:

1. **dateFieldSerialisesAsNumericTimestamp()**: Serialises a `java.util.Date` and checks if the JSON contains an unquoted numeric value (Jackson 2.x) or a quoted ISO string (Jackson 3.x)
2. **numericTimestampHasNoQuotes()**: Verifies that numeric timestamps have no surrounding quotes (2.x behavior)
3. **multipleFormatsPreserveConsistency()**: Tests that multiple date fields use consistent formatting

The test uses a Map-based approach rather than direct Jackson imports to remain compatible with both Jackson 2.x and Jackson 3.x package names.

## On Spring Boot 3.5.14 (Jackson 2.x)

```bash
mvn test
```

All three tests pass. Example output:
```
✓ dateFieldSerialisesAsNumericTimestamp (JSON contains unquoted 1699257000000)
✓ numericTimestampHasNoQuotes (timestamp is a number, not a string)
✓ multipleFormatsPreserveConsistency (both dates are numeric)
```

## On Spring Boot 4.0 (Jackson 3.x)

```bash
mvn test
```

All three tests fail. Example failures:
```
✗ dateFieldSerialisesAsNumericTimestamp
  Expected numeric timestamp (millis) for java.util.Date.
  If this fails, Jackson has switched to ISO-8601 strings by default.
  JSON: {"occurredAt":"2023-11-06T05:30:00.000+00:00"}

✗ numericTimestampHasNoQuotes
  Expected unquoted numeric value for Date, but got a quoted string.
  Jackson 3 defaults to ISO-8601 strings.
```

## Fix / Migration Path

### Option 1: Restore Jackson 2.x Behavior (Most Compatible)

Configure Jackson to write dates as numeric timestamps via Spring Boot properties:

```properties
# application.properties
spring.jackson.serialization.write-dates-as-timestamps=true
```

Or programmatically:

```java
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer dateFormatCustomizer() {
        return builder -> builder.featuresToEnable(
            com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }
}
```

This restores the Jackson 2.x format for all date fields application-wide.

### Option 2: Adopt ISO-8601 and Update Clients (Long-term Solution)

Update assertions and client code to expect ISO-8601 strings:

```java
// In DateSerializationTest.java
assertTrue(
    json.contains("\"occurredAt\":\"2023-11-06"),
    "Expected ISO-8601 timestamp string. JSON: " + json
);
```

Update client code (JavaScript, mobile apps, etc.) to parse ISO-8601 strings:
```javascript
const date = new Date("2023-11-06T05:30:00Z");
```

### Option 3: Field-Level Control via @JsonFormat

Use Jackson's `@JsonFormat` annotation to control serialisation per field:

```java
public class Event {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date occuredAt;  // Always numeric
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date createdAt;  // Always ISO-8601 string
}
```

This allows mixed formats within the same class if needed.

## References

- Jackson 3.0 Release Notes: https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0
- Jackson 3.0 Changes (includes serialisation defaults): https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
