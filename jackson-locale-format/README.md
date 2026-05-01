# Jackson Locale Serialisation Format Change Migration Test

## One-Line Summary
Jackson 3.0 (Spring Boot 4.0) changes locale serialisation from underscores (Java format "zh_CN") to hyphens (IETF BCP 47 "zh-CN"), silently breaking caching and i18n lookups.

## What Breaks

Jackson 3.0 bundled with Spring Boot 4.0 changes how it serialises `java.util.Locale` objects to JSON:

**Spring Boot 3.5.14 (Jackson 2.x):** `Locale.CHINA` → `"zh_CN"` (Java's `Locale.toString()`)
**Spring Boot 4.0 (Jackson 3.x):** `Locale.CHINA` → `"zh-CN"` (IETF BCP 47 via `Locale.toLanguageTag()`)

This is a **silent change with no compilation or runtime error**. The locale format changes, breaking code that depends on:
- Caching layers using locale strings as cache keys
- I18n resource bundle lookups expecting underscore format
- REST API contracts expecting "zh_CN" format
- Configuration or properties files keyed by locale string
- Third-party integrations expecting Java's legacy underscore format

## How This Test Works

The test suite uses Java reflection to load Jackson's `ObjectMapper` and verifies locale serialisation format via direct JSON inspection:

1. **chineseLocaleUsesUnderscoreFormat()**: Serialises `Locale.CHINA` and checks for "zh_CN" (Jackson 2.x)
2. **frenchLocaleUsesUnderscoreFormat()**: Serialises `Locale.FRANCE` and checks for "fr_FR" (Jackson 2.x)
3. **usLocaleUsesUnderscoreFormat()**: Serialises `Locale.US` and checks for "en_US" (Jackson 2.x)

All three tests expect underscores. On Jackson 3.x, they receive hyphens instead and fail.

The test uses a Map-based approach with reflection to remain compatible with both Jackson 2.x and Jackson 3.x package names.

## On Spring Boot 3.5.14 (Jackson 2.x)

```bash
mvn test
```

All three tests pass. Example output:
```
✓ chineseLocaleUsesUnderscoreFormat (JSON: {"locale":"zh_CN"})
✓ frenchLocaleUsesUnderscoreFormat (JSON: {"locale":"fr_FR"})
✓ usLocaleUsesUnderscoreFormat (JSON: {"locale":"en_US"})
```

## On Spring Boot 4.0 (Jackson 3.x)

```bash
mvn test
```

All three tests fail. Example failures:
```
✗ chineseLocaleUsesUnderscoreFormat
  Expected locale in Java toString() format (zh_CN) but got BCP 47 format.
  JSON: {"locale":"zh-CN"}

✗ frenchLocaleUsesUnderscoreFormat
  Expected locale in Java format (fr_FR) but got BCP 47 format.
  JSON: {"locale":"fr-FR"}

✗ usLocaleUsesUnderscoreFormat
  Expected locale in Java format (en_US) but got BCP 47 format.
  JSON: {"locale":"en-US"}
```

## Fix / Migration Path

### Option 1: Accept BCP 47 and Update Consumers (Recommended, Long-term)

IETF BCP 47 is the international standard format. Update code that consumes locale strings:

```java
// Before: expects "zh_CN"
String localeString = "zh_CN";
Locale locale = Locale.of(localeString.replace('_', '-'));

// After: expects "zh-CN"
String localeString = "zh-CN";
Locale locale = Locale.forLanguageTag(localeString);
```

Update i18n resource bundle lookups:
```properties
# messages_zh_CN.properties  → messages_zh-CN.properties (rename files)
# Or update code to use forLanguageTag()
```

Update cache key generation:
```java
// Before
String cacheKey = "user:" + locale.toString();  // "user:zh_CN"

// After
String cacheKey = "user:" + locale.toLanguageTag();  // "user:zh-CN"
```

### Option 2: Restore Jackson 2.x Behavior (Quickest Compatibility Fix)

Configure Jackson to use Java's `Locale.toString()` format via a custom serialiser:

```java
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localeFormatCustomizer() {
        return builder -> builder.serializerByType(Locale.class, new LocaleSerializer());
    }
    
    static class LocaleSerializer extends StdSerializer<Locale> {
        public LocaleSerializer() {
            super(Locale.class);
        }
        
        @Override
        public void serialize(Locale value, JsonGenerator gen, SerializerProvider provider) 
                throws IOException {
            // Use Java's legacy toString() format instead of BCP 47
            gen.writeString(value.toString());  // "zh_CN" instead of "zh-CN"
        }
    }
}
```

### Option 3: Field-Level Format Control via @JsonFormat

Use Jackson's `@JsonFormat` annotation to control serialisation per field:

```java
public class UserPreferences {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Locale preferredLocale;  // Serialises as BCP 47
}
```

Or create a custom annotation:

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JsonSerialize(using = LegacyLocaleSerializer.class)
public @interface LegacyLocale { }

public class UserPreferences {
    @LegacyLocale
    private Locale preferredLocale;  // Serialises as "zh_CN"
}
```

### Option 4: Update Test Assertions

If changing code is not feasible immediately, update test assertions:

```java
// Before
assertTrue(json.contains("zh_CN"));

// After (Spring Boot 4.0)
assertTrue(json.contains("zh-CN"));
```

## BCP 47 Format Reference

IETF BCP 47 is the standard format for language tags. Key differences from Java's `Locale.toString()`:

| Java (toString) | BCP 47 (toLanguageTag) | Description |
|---|---|---|
| `en` | `en` | English (same) |
| `en_US` | `en-US` | English, United States |
| `zh_CN` | `zh-CN` | Simplified Chinese |
| `zh_TW` | `zh-TW` | Traditional Chinese |
| `pt_BR` | `pt-BR` | Portuguese, Brazil |
| `de_DE` | `de-DE` | German, Germany |

BCP 47 uses **hyphens** as the separator; Java uses **underscores**.

## References

- Jackson 3.0 Release Notes: https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0
- Jackson 3.0 Changes: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
- IETF BCP 47 Language Tags: https://tools.ietf.org/html/bcp47
- Java Locale.toLanguageTag() JavaDoc: https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html#toLanguageTag--
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
