package com.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the silent locale format change between Jackson 2.x and 3.x.
 *
 * Jackson 2.x serialises Locale using Java's Locale.toString():
 *   Locale.CHINA  → "zh_CN"
 *   Locale.FRANCE → "fr_FR"
 *   Locale.US     → "en_US"
 *
 * Jackson 3.x serialises Locale using IETF BCP 47 (Locale.toLanguageTag()):
 *   Locale.CHINA  → "zh-CN"
 *   Locale.FRANCE → "fr-FR"
 *   Locale.US     → "en-US"
 *
 * This is a Tier 3 "Wrong Results" breaking change: no compilation or runtime error,
 * but the serialisation format changes silently. Code depending on underscore format
 * (caching, i18n resource bundle lookups, API contracts) breaks unexpectedly.
 *
 * References:
 * - Jackson 3.0 Release Notes: https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0
 * - Jackson 3.0 Changes: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
 * - IETF BCP 47: https://tools.ietf.org/html/bcp47
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 *
 * Uses reflection to load ObjectMapper from whichever Jackson package is on the
 * classpath, so this test compiles on both Jackson 2 and Jackson 3.
 */
class LocaleSerializationTest {

    @Test
    void chineseLocaleUsesUnderscoreFormat() throws Exception {
        String json = serialise(Locale.CHINA);
        System.out.println("Serialised Locale.CHINA: " + json);

        // On Jackson 2.x: "zh_CN" — passes
        // On Jackson 3.x: "zh-CN" — fails
        assertTrue(
            json.contains("zh_CN"),
            "Expected locale in Java toString() format (zh_CN) but got BCP 47 format. " +
            "JSON: " + json
        );
    }

    @Test
    void frenchLocaleUsesUnderscoreFormat() throws Exception {
        String json = serialise(Locale.FRANCE);

        assertTrue(
            json.contains("fr_FR"),
            "Expected locale in Java format (fr_FR) but got BCP 47 format. JSON: " + json
        );
    }

    @Test
    void usLocaleUsesUnderscoreFormat() throws Exception {
        String json = serialise(Locale.US);

        assertTrue(
            json.contains("en_US"),
            "Expected locale in Java format (en_US) but got BCP 47 format. JSON: " + json
        );
    }

    /**
     * Serialise a Locale value via Jackson's ObjectMapper using reflection.
     * Wraps the locale in a Map so Jackson treats it as a field value, not a root.
     */
    private String serialise(Locale locale) throws Exception {
        Object mapper = createObjectMapper();
        Method writeValueAsString = mapper.getClass()
            .getMethod("writeValueAsString", Object.class);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("locale", locale);

        return (String) writeValueAsString.invoke(mapper, data);
    }

    private Object createObjectMapper() throws Exception {
        Class<?> omClass;
        try {
            omClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        } catch (ClassNotFoundException e) {
            omClass = Class.forName("tools.jackson.databind.ObjectMapper");
        }
        return omClass.getDeclaredConstructor().newInstance();
    }
}
