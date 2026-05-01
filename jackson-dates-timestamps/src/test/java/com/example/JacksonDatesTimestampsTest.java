package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Category (d) — Different Results on Boot 4.0
 *
 * Tests that spring.jackson.serialization.write-dates-as-timestamps=true
 * is honoured by the auto-configured ObjectMapper.
 *
 * Boot 3.5 (Jackson 2.x): property applied → Instant serialises as numeric millis
 *   {"ts":1714089600000}
 *
 * Boot 4.0 (Jackson 3.0): property silently ignored → Instant serialises as ISO string
 *   {"ts":1.71408960E9}  or  {"ts":"2025-04-26T00:00:00Z"}
 *
 * The app starts and runs on both. No error is thrown. The JSON shape changes silently.
 *
 * Fix: replace the property with a Jackson2ObjectMapperBuilderCustomizer bean that
 * explicitly enables SerializationFeature.WRITE_DATES_AS_TIMESTAMPS.
 */
@SpringBootTest(properties = {
    "spring.jackson.serialization.write-dates-as-timestamps=true"
})
class JacksonDatesTimestampsTest {

    @Autowired
    private ApplicationContext ctx;

    private static final long EPOCH_MILLIS = 1714089600000L; // 2025-04-26T00:00:00Z

    @Test
    void writeDatesAsTimestampsPropertyIsHonoured() throws Exception {
        Object mapper = resolveObjectMapper();
        java.lang.reflect.Method writeValueAsString =
            mapper.getClass().getMethod("writeValueAsString", Object.class);

        // Use a Map with an Instant — unambiguously a date/time type
        Map<String, Object> payload = Map.of("ts", Instant.ofEpochMilli(EPOCH_MILLIS));
        String json = (String) writeValueAsString.invoke(mapper, payload);

        // Boot 3.5: property honoured → numeric value, no quotes around the number
        // Boot 4.0: property ignored → ISO-8601 string, value is quoted
        assertFalse(
            json.contains("\"ts\":\""),
            "spring.jackson.serialization.write-dates-as-timestamps=true was set " +
            "but Instant serialised as a quoted string, not a numeric timestamp. " +
            "The property is being silently ignored. JSON: " + json
        );

        assertTrue(
            json.contains("\"ts\":"),
            "Expected 'ts' field in JSON. Got: " + json
        );
    }

    @Test
    void numericTimestampHasNoIsoChars() throws Exception {
        Object mapper = resolveObjectMapper();
        java.lang.reflect.Method writeValueAsString =
            mapper.getClass().getMethod("writeValueAsString", Object.class);

        Map<String, Object> payload = Map.of("created", Instant.ofEpochMilli(EPOCH_MILLIS));
        String json = (String) writeValueAsString.invoke(mapper, payload);

        // ISO-8601 strings contain 'T' and 'Z'; numeric timestamps do not
        assertFalse(
            json.contains("T") && json.contains("Z"),
            "Date serialised as ISO-8601 string despite write-dates-as-timestamps=true. " +
            "Property is silently ignored in Boot 4.0. JSON: " + json
        );
    }

    private Object resolveObjectMapper() throws Exception {
        try {
            Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            return ctx.getBean(cls);
        } catch (ClassNotFoundException e) {
            Class<?> cls = Class.forName("tools.jackson.databind.ObjectMapper");
            return ctx.getBean(cls);
        }
    }
}
