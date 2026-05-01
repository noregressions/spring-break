package com.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the silent date serialisation format change between Jackson 2.x and 3.x.
 *
 * Jackson 2.x default: WRITE_DATES_AS_TIMESTAMPS = true
 *   java.util.Date → 1699257000000 (numeric millis)
 *
 * Jackson 3.x default: WRITE_DATES_AS_TIMESTAMPS = false
 *   java.util.Date → "2023-11-06T05:30:00Z" (ISO-8601 string)
 *
 * This is a Tier 3 "Wrong Results" breaking change: no compilation or runtime error,
 * but the JSON format changes silently. API clients expecting numeric timestamps receive
 * ISO-8601 strings instead. Mobile apps, frontend code, and contract tests break.
 *
 * References:
 * - Jackson 3.0 Release Notes: https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0
 * - Jackson 3.0 Changes: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 *
 * Uses reflection to load ObjectMapper from whichever Jackson package is on the
 * classpath (com.fasterxml.jackson or tools.jackson), so this test compiles on both.
 */
class DateSerializationTest {

    private static final long TIMESTAMP = 1699257000000L; // 2023-11-06T05:30:00Z

    @Test
    void dateFieldSerialisesAsNumericTimestamp() throws Exception {
        Object mapper = createObjectMapper();
        Method writeValueAsString = mapper.getClass()
            .getMethod("writeValueAsString", Object.class);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("id", 1L);
        event.put("name", "Conference Talk");
        event.put("occurredAt", new Date(TIMESTAMP));

        String json = (String) writeValueAsString.invoke(mapper, event);
        System.out.println("JSON: " + json);

        // On Jackson 2.x: Date → numeric millis: {"occurredAt":1699257000000}
        // On Jackson 3.x: Date → ISO-8601 string: {"occurredAt":"2023-11-06T05:30:00.000+00:00"}
        assertTrue(
            json.contains("\"occurredAt\":1") || json.contains("\"occurredAt\":16"),
            "Expected numeric timestamp (millis) for java.util.Date. " +
            "If this fails, Jackson has switched to ISO-8601 strings by default. " +
            "JSON: " + json
        );
    }

    @Test
    void numericTimestampHasNoQuotes() throws Exception {
        Object mapper = createObjectMapper();
        Method writeValueAsString = mapper.getClass()
            .getMethod("writeValueAsString", Object.class);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ts", new Date(TIMESTAMP));

        String json = (String) writeValueAsString.invoke(mapper, data);

        // On Jackson 2.x: "ts":1699257000000 (no quotes around the number)
        // On Jackson 3.x: "ts":"2023-11-06..." (quoted string)
        assertFalse(
            json.contains("\"ts\":\""),
            "Expected unquoted numeric value for Date, but got a quoted string. " +
            "Jackson 3 defaults to ISO-8601 strings. JSON: " + json
        );
    }

    @Test
    void multipleFormatsPreserveConsistency() throws Exception {
        Object mapper = createObjectMapper();
        Method writeValueAsString = mapper.getClass()
            .getMethod("writeValueAsString", Object.class);

        // Serialise two different dates to verify consistent formatting
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("created", new Date(TIMESTAMP));
        data.put("updated", new Date(TIMESTAMP + 86400000L)); // +1 day

        String json = (String) writeValueAsString.invoke(mapper, data);

        // Both should be numeric on 2.x, both ISO strings on 3.x.
        // If this passes (both numeric), the test suite is on Jackson 2.x.
        long quoteCount = json.chars().filter(c -> c == '"').count();
        // On 2.x: {"created":169..,"updated":169..} → 4 quotes (just the keys)
        // On 3.x: {"created":"2023-..","updated":"2023-.."} → 8 quotes (keys + values)
        assertTrue(
            quoteCount <= 6,
            "Expected numeric (unquoted) Date values. Got " + quoteCount +
            " quote characters — suggests ISO-8601 strings. JSON: " + json
        );
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
