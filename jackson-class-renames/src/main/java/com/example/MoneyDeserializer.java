package com.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * Custom deserializer demonstrating Jackson 2.x extension API removed in Jackson 3.0.
 *
 * On Boot 3.5 (Jackson 2.x): extends JsonDeserializer<T> with DeserializationContext — compiles.
 * On Boot 4.0 (Jackson 3.0):
 *   - JsonDeserializer renamed to ValueDeserializer
 *   - DeserializationContext package changed (tools.jackson)
 *   Compilation fails: cannot find symbol class JsonDeserializer
 */
public class MoneyDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        // Parse a decimal string back to cents: "10.99" → 1099
        String value = p.getText().trim();
        return Math.round(Double.parseDouble(value) * 100);
    }
}
