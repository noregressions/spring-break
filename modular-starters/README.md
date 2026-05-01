# Modular Starters: Implicit Auto-Configuration Removed

Spring Boot 4.0 splits monolithic auto-configuration into focused modules; implicit beans from transitive starters disappear.

## What Breaks

Spring Boot 3.x shipped a monolithic `spring-boot-autoconfigure` that bundled all auto-configuration classes. This was included transitively by all starters, so features like Jackson, validation, etc. were auto-configured by default even without explicit dependencies.

Spring Boot 4.0 restructures auto-configuration into modular, focused packages. Auto-configuration only activates when the relevant starter is explicitly on the classpath.

**Example: Jackson ObjectMapper**

Spring Boot 3.4.1 with only `spring-boot-starter-web`:
- Monolithic auto-configure includes JacksonAutoConfiguration
- ObjectMapper bean created automatically
- `@Autowired ObjectMapper` works without explicit dependency

Spring Boot 4.0 with only `spring-boot-starter-web`:
- Jackson auto-config only activates if `spring-boot-starter-json` is present
- ObjectMapper bean NOT created
- `@Autowired ObjectMapper` fails with NoSuchBeanDefinitionException

Similar issues occur with:
- Validation (needs `spring-boot-starter-validation`)
- Data formats (XML, CSV)
- Template engines (Thymeleaf, FreeMarker)
- Caching (Redis, Caffeine)

## How This Test Works

The test attempts to autowire an ObjectMapper bean and verify it functions:

- **objectMapperBeanIsAutoConfigured()**: Attempts `@Autowired ObjectMapper`. On 3.4.1, the monolithic auto-configure provides it. On 4.0, it fails unless `spring-boot-starter-json` is explicitly added.
- **objectMapperCanSerialize()**: Verifies the autowired ObjectMapper is functional (serialize a map to JSON).

The tests demonstrate that even with `spring-boot-starter-web` (which includes Jackson as a transitive dependency), the auto-configuration bean is not created in Spring Boot 4.0.

## On Spring Boot 3.4.1

```bash
mvn clean test
```

Output: Both tests pass. ObjectMapper is auto-configured by the monolithic module.

## On Spring Boot 4.0

First test fails at injection:
```
NoSuchBeanDefinitionException: No qualifying bean of type 'com.fasterxml.jackson.databind.ObjectMapper' available
```

The ObjectMapper class exists (Jackson is a dependency), but no bean was created because Jackson auto-configuration didn't activate.

## Fix / Migration Path

**Option 1: Add explicit starters (recommended)**

Add the specific starters your application uses:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Add these explicitly for 4.0 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-json</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Option 2: Create beans programmatically**

If you prefer minimal dependencies:

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

**Audit your application:**

```bash
# Find all @Autowired dependencies that may be missing
grep -r "@Autowired" src/main/java | grep -E "ObjectMapper|Validator|MessageSource"

# Check which starters are in pom.xml
grep "spring-boot-starter" pom.xml

# Common starters that may be missing:
# - spring-boot-starter-json (Jackson)
# - spring-boot-starter-validation (Jakarta Validation)
# - spring-boot-starter-actuator (Micrometer)
# - spring-boot-starter-cache (Cache abstraction)
```

**Migration checklist:**

- [ ] Audit pom.xml for explicit vs. transitive dependencies
- [ ] Add missing `spring-boot-starter-*` modules for all features used
- [ ] Update `@Configuration` classes to enable auto-configuration
- [ ] Run `mvn clean test` to verify beans are created
- [ ] Check for NoSuchBeanDefinitionException errors
- [ ] Update application.properties if needed (auto-config properties unchanged)

## Common Missing Starters

| Feature | Required Starter | Bean Affected |
|---------|------------------|---------------|
| JSON (Jackson) | `spring-boot-starter-json` | ObjectMapper, JsonFactory |
| Validation | `spring-boot-starter-validation` | Validator, LocalValidatorFactoryBean |
| Actuator | `spring-boot-starter-actuator` | MeterRegistry, HealthEndpoint |
| Cache | `spring-boot-starter-cache` | CacheManager |
| Templating | `spring-boot-starter-thymeleaf` | ThymeleafViewResolver |
| Database | `spring-boot-starter-data-jpa` | JpaRepository, EntityManager |

## References

- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
- Spring Boot Starters Documentation: https://spring.io/projects/spring-boot#learn
