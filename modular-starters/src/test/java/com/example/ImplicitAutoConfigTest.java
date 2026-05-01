package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (c) — Runtime Errors on 4.0
 *
 * Demonstrates the modular auto-configuration breaking change in Spring Boot 4.0.
 *
 * Spring Boot 3.x (monolithic autoconfigure):
 *   spring-boot-starter-web includes the monolithic spring-boot-autoconfigure jar.
 *   JacksonAutoConfiguration runs automatically, creating an ObjectMapper bean.
 *   No explicit spring-boot-starter-json dependency needed.
 *
 * Spring Boot 4.0 (modular autoconfigure):
 *   Auto-configuration split into feature-specific modules.
 *   Jackson auto-config only activates with explicit spring-boot-starter-json.
 *   Without it, ObjectMapper bean is missing → NoSuchBeanDefinitionException.
 *
 * On Boot 3.5: compiles and passes — ObjectMapper bean auto-configured.
 * On Boot 4.0:   compiles, but ctx.getBean() throws NoSuchBeanDefinitionException.
 *
 * This test uses reflection to avoid importing Jackson classes directly (the
 * package rename from com.fasterxml to tools.jackson is a separate issue
 * covered by the jackson-group-id module).
 *
 * Fix: Add spring-boot-starter-json explicitly to pom.xml.
 *
 * References:
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
@SpringBootTest
class ImplicitAutoConfigTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void objectMapperBeanIsAutoConfigured() throws Exception {
        // Resolve the ObjectMapper class from whichever Jackson package is present
        Class<?> objectMapperClass = resolveObjectMapperClass();

        // On Boot 3.x: monolithic autoconfigure creates ObjectMapper bean → passes
        // On Boot 4.0: modular autoconfigure requires spring-boot-starter-json
        //   Without it → NoSuchBeanDefinitionException
        Map<String, ?> beans = ctx.getBeansOfType(objectMapperClass);
        assertFalse(beans.isEmpty(),
                "ObjectMapper should be auto-configured via spring-boot-starter-web. " +
                "On Boot 4.0, add spring-boot-starter-json explicitly.");
    }

    @Test
    void objectMapperCanSerialize() throws Exception {
        Class<?> objectMapperClass = resolveObjectMapperClass();
        Object mapper = ctx.getBean(objectMapperClass);

        // Verify the bean is functional, not just present
        java.lang.reflect.Method writeValueAsString =
                objectMapperClass.getMethod("writeValueAsString", Object.class);
        String json = (String) writeValueAsString.invoke(mapper,
                Map.of("key", "value", "number", 42));

        assertNotNull(json);
        assertTrue(json.contains("value"), "JSON should contain serialized value");
    }

    /**
     * Resolves ObjectMapper class from whichever Jackson package is on the classpath.
     * Jackson 2.x: com.fasterxml.jackson.databind.ObjectMapper
     * Jackson 3.x: tools.jackson.databind.ObjectMapper
     */
    private Class<?> resolveObjectMapperClass() throws ClassNotFoundException {
        try {
            return Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        } catch (ClassNotFoundException e) {
            return Class.forName("tools.jackson.databind.ObjectMapper");
        }
    }
}
