# Deprecated Classes Removed (Tier 1: Won't Launch)

**Summary**: Spring Boot 4.0 removes all classes deprecated across the 2.x and 3.x release cycles, breaking code that ignored deprecation warnings.

## What Breaks

Spring Boot 4.0 **removes** entire classes and packages that were deprecated in earlier versions:

1. **RestTemplate auto-configuration removed** — `RestTemplate` is no longer auto-configured; must be manually declared as a bean
2. **Deprecated builder methods removed** — `RestTemplateBuilder.additionalMessageConverters()` and similar deprecated methods are gone
3. **Legacy embedded server config removed** — Old embedded servlet container customization patterns no longer work
4. **Removal of org.springframework.boot.context.embedded classes** — Entire packages cleaned up

This is a **Tier 1 failure**: code using deprecated APIs fails to compile on Boot 4.0.

## How This Test Works

The test module demonstrates deprecated APIs that compile with warnings on Boot 3.5.14 but fail to compile on Boot 4.0:

- **DeprecatedUsageDemo.java**: Uses deprecated APIs (`RestTemplate` auto-configuration, deprecated builder methods, legacy container customization)
- **DeprecatedUsageTest.java**: Tests that the deprecated APIs work correctly on Boot 3.5.14

## On Spring Boot 3.5.14

```bash
mvn clean compile
```

**Result**: ✓ Compiles successfully (with deprecation warnings):

```
[WARNING] EmbeddedServletContainerCustomizer in org.springframework.boot.context.embedded is deprecated
[WARNING] TomcatEmbeddedServletContainerFactory is deprecated
```

Application runs and serves HTTP requests on port 8080.

## On Spring Boot 4.0

```bash
mvn clean compile
```

**Result**: ✗ Compilation fails:

```
[ERROR] error: cannot find symbol: class TomcatServletWebServerFactory
[ERROR] error: package org.springframework.boot.context.embedded does not exist
```
[ERROR]   location: package org.springframework.boot.context.embedded
[ERROR] /path/to/DeprecatedUsageDemo.java:25: error: cannot find symbol
[ERROR]   symbol:   class TomcatEmbeddedServletContainerFactory
```

The classes no longer exist in the classpath.

## Fix / Migration Path

### 1. Migrate RestTemplate to RestClient or declare as @Bean

Spring Boot 4.0 removed the RestTemplate auto-configuration. Declare it as a bean:

```java
// OLD (auto-configured, deprecated on 3.x)
@Autowired
private RestTemplate restTemplate;

// NEW (explicit bean declaration)
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// OR use the newer RestClient
@Bean
public RestClient restClient() {
    return RestClient.create();
}
```

### 2. Update WebServerFactoryCustomizer

Use generic WebServerFactoryCustomizer instead of concrete types:

```java
// OLD (Boot 3.x)
@Bean
public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
    return factory -> factory.setPort(8080);
}

// NEW (Boot 4.0 - use generic interface)
@Bean
public WebServerFactoryCustomizer<?> containerCustomizer() {
    return factory -> {
        if (factory instanceof TomcatServletWebServerFactory) {
            ((TomcatServletWebServerFactory) factory).setPort(8080);
        }
    };
}
```

### 3. Search and Replace Deprecated Classes

Common deprecated classes removed in Boot 4.0:
- `org.springframework.boot.context.embedded.*` — all classes removed
- `EmbeddedServletContainerCustomizer` — use `WebServerFactoryCustomizer`
- `EmbeddedServletContainerFactory` — use `WebServerFactory`
- RestTemplate-related builder methods — use RestClient instead

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)

1. **Missing package**: `org.springframework.boot.context.embedded.*` completely removed
2. **No backward compatibility shim**: Unlike previous major versions, 4.0 does not provide deprecated stubs
3. **Direct compilation errors**: Code won't compile, not just produce warnings
4. **Cascading failures**: Any Bean that references these classes fails to instantiate

## The Fix

Replace deprecated embedded server configuration with the modern `WebServerFactoryCustomizer` pattern:

**Before (Spring Boot 3.x - Deprecated):**
```java
@Bean
public EmbeddedServletContainerCustomizer containerCustomizer() {
    return container -> {
        if (container instanceof TomcatEmbeddedServletContainerFactory) {
            TomcatEmbeddedServletContainerFactory tomcat =
                (TomcatEmbeddedServletContainerFactory) container;
            tomcat.setPort(8080);
        }
    };
}

@Bean
public TomcatEmbeddedServletContainerFactory embeddedServletContainerFactory() {
    TomcatEmbeddedServletContainerFactory factory = 
        new TomcatEmbeddedServletContainerFactory();
    factory.setPort(8080);
    return factory;
}
```

**After (Spring Boot 4.0 - Modern):**
```java
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

@Bean
public WebServerFactoryCustomizer<TomcatServletWebServerFactory> 
    webServerFactoryCustomizer() {
    return factory -> factory.setPort(8080);
}
```

**Or use properties instead:**
```properties
server.port=8080
```

## Tier 1 Issue

This is a **Tier 1 breaking change**: code using deprecated classes will not compile on Spring Boot 4.0. All deprecated API usage must be refactored during upgrade.

## Migration Strategy

1. **Audit codebase**: Search for imports from `org.springframework.boot.context.embedded.*`
2. **Replace with WebServerFactoryCustomizer**: Modern pattern for all servlet container customization
3. **Use application properties**: For simple configuration (port, context-path, etc.), prefer `application.properties` or `application.yml`
4. **Test thoroughly**: Embedded server configuration often affects startup behavior
