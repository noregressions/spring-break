# Jackson Group ID Breaking Change (Tier 1: Won't Launch)

**Summary**: Jackson 3.0 migration in Spring Boot 4.0 changes the Maven group ID from `com.fasterxml.jackson` to `tools.jackson`, breaking all imports at compile time.

## What Breaks

Spring Boot 4.0 upgrades Jackson from 2.x to 3.0, which includes a **breaking change** to the Maven group ID and package names. All code using Jackson 2.x APIs fails to compile on Boot 4.0 because:

1. **Group ID Changed**: `com.fasterxml.jackson` → `tools.jackson` (all dependencies)
2. **Classes Renamed**: `JsonSerializer` → `ValueSerializer`, `DeserializationContext` → `DeserializationContext`, etc.
3. **Package Moved**: All Jackson classes move from `com.fasterxml.jackson.*` to `tools.jackson.*`
4. **Annotations Bundled**: `@JsonSerialize`, `@JsonDeserialize` move to `tools.jackson.databind.annotation`

This is a **Tier 1 failure**: your build breaks immediately with `package does not exist` errors.

## How This Test Works

The test module contains Jackson 2.x code that compiles cleanly on Spring Boot 3.5.14 but fails to compile on Boot 4.0:

- **JacksonGroupIdDemo.java**: Uses Jackson 2.x APIs (`com.fasterxml.jackson.*` imports, `JsonSerializer` class)
- **JacksonGroupIdTest.java**: Two test cases validating Jackson serialization using legacy APIs

**On Spring Boot 3.5.14**: Both tests pass; the code compiles and runs.

**On Spring Boot 4.0**: Compilation fails immediately with errors like:
```
error: package com.fasterxml.jackson.databind does not exist
error: cannot find symbol: class JsonSerializer
```

## On Spring Boot 3.5.14

```bash
mvn clean compile
```

**Result**: ✓ Compilation succeeds.

```bash
mvn exec:java -Dexec.mainClass="com.example.JacksonGroupIdDemo"
```

**Output**:
```
Serialized: {"name":"[CUSTOM] Alice","age":30}
```

## On Spring Boot 4.0

```bash
mvn clean compile
```

**Result**: ✗ Compilation fails with multiple errors:
```
[ERROR] error: package com.fasterxml.jackson.databind does not exist
[ERROR] error: cannot find symbol: class JsonSerializer
[ERROR] error: cannot find symbol: @JsonSerialize
```

## Fix / Migration Path

### 1. Update pom.xml

Replace all `com.fasterxml.jackson` group IDs with `tools.jackson`:

```xml
<!-- OLD -->
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
</dependency>

<!-- NEW -->
<dependency>
  <groupId>tools.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
</dependency>
```

### 2. Update All Jackson Imports

Replace the import paths:

```java
// OLD (Jackson 2.x)
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

// NEW (Jackson 3.0)
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.annotation.JsonSerialize;
```

### 3. Rename Serializer/Deserializer Classes

Key class renames in Jackson 3.0:
- `JsonSerializer` → `ValueSerializer`
- `JsonDeserializer` → `ValueDeserializer`
- `SerializationConfig` → `SerializationConfiguration`
- `DeserializationConfig` → `DeserializationConfiguration`

```java
// OLD
public class NamePrefixSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeString("[CUSTOM] " + value);
    }
}

// NEW
public class NamePrefixSerializer extends ValueSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext context)
            throws IOException {
        gen.writeString("[CUSTOM] " + value);
    }
}
```

### 4. Use IDE Tools

- Use your IDE's "Find and Replace" to update all imports
- Search for `com.fasterxml.jackson` and replace with `tools.jackson`
- Search for `extends JsonSerializer` and replace with `extends ValueSerializer`
- Run `mvn clean compile` frequently to catch remaining issues

## References

- [Jackson 3.0 Release Notes](https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0)
- [Jackson 3.0 Changes Guide](https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
