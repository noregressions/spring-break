# Jackson Customiser Renamed (Tier 1: Won't Compile)

**Summary**: Spring Boot 4.0 renames `Jackson2ObjectMapperBuilderCustomizer` to `JsonMapperBuilderCustomizer`, moves it to a new package (`org.springframework.boot.jackson.autoconfigure`), and ships it in a new module (`spring-boot-jackson`). Code that imports the old class fails to compile.

> [!note] Module name vs content
> This module's folder is named `jackson-property-inclusion` for historical reasons (it previously demonstrated `spring.jackson.default-property-inclusion` behaviour, which turned out not to be a real Boot 4.0 break — see the audit). The current demo shows the customiser rename, which IS a real Tier 1 break.

## What breaks

In Spring Boot 3.5, applications that wanted to tweak the auto-configured `ObjectMapper` registered a bean of type `Jackson2ObjectMapperBuilderCustomizer`:

```java
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

@Bean
public Jackson2ObjectMapperBuilderCustomizer excludeNullsCustomiser() {
    return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL);
}
```

Spring Boot 4.0 removes this class entirely. Three things change at once:

| | Boot 3.5 | Boot 4.0 |
|---|---|---|
| Class name | `Jackson2ObjectMapperBuilderCustomizer` | `JsonMapperBuilderCustomizer` |
| Package | `org.springframework.boot.autoconfigure.jackson` | `org.springframework.boot.jackson.autoconfigure` |
| Artifact | `spring-boot-autoconfigure` | `spring-boot-jackson` (new modular starter) |

Existing imports fail at compile time:

```
package org.springframework.boot.autoconfigure.jackson does not exist
cannot find symbol: class Jackson2ObjectMapperBuilderCustomizer
```

## How this test works

`JacksonCustomiserConfig` registers a `Jackson2ObjectMapperBuilderCustomizer` bean and `JacksonPropertyInclusionTest` instantiates one to confirm the class is on the classpath.

On Boot 3.5: the import resolves, both compile cleanly, the test passes.

On Boot 4.0: the import fails. The build never reaches the test phase.

## Fix / Migration Path

Update the import and the `@Bean` type, and depend on the new modular starter:

```xml
<!-- pom.xml — depend on the new spring-boot-jackson module if not already pulled in -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-jackson</artifactId>
</dependency>
```

```java
// Before
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

@Bean
public Jackson2ObjectMapperBuilderCustomizer excludeNulls() { ... }
```

```java
// After
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

@Bean
public JsonMapperBuilderCustomizer excludeNulls() { ... }
```

The `JsonMapperBuilderCustomizer` interface accepts a `JsonMapper.Builder` (Jackson 3) rather than a `Jackson2ObjectMapperBuilder`. The lambda body usually carries over with minor adjustments — most of the configuration methods on `JsonMapper.Builder` mirror the old builder.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) — "Jackson" section covers the customiser, `@JsonComponent` → `@JacksonComponent`, and `@JsonMixin` → `@JacksonMixin` renames
- [Jackson 3 Migration Guide](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md)
- [Spring Blog — Jackson 3 Support](https://spring.io/blog/2025/10/07/introducing-jackson-3-support-in-spring/)
