package com.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Custom serializer demonstrating Jackson 2.x extension API removed in Jackson 3.0.
 *
 * On Boot 3.5 (Jackson 2.x): extends JsonSerializer<T> with SerializerProvider — compiles.
 * On Boot 4.0 (Jackson 3.0):
 *   - JsonSerializer renamed to ValueSerializer
 *   - SerializerProvider renamed to SerializationContext
 *   Compilation fails: cannot find symbol class JsonSerializer
 *
 * Master list: jackson class renames (JsonSerializer, JsonDeserializer, SerializerProvider)
 */
public class MoneySerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long cents, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        // Format cents as a decimal currency string: 1099 → "10.99"
        gen.writeString(String.format("%.2f", cents / 100.0));
    }
}
