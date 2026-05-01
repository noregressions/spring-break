package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (a) — Won't Compile on Boot 4.0
 *
 * On Boot 3.5 (Jackson 2.x): MoneySerializer extends JsonSerializer,
 *   MoneyDeserializer extends JsonDeserializer — all compile and work correctly.
 *
 * On Boot 4.0 (Jackson 3.0): com.fasterxml.jackson.databind.JsonSerializer
 *   and JsonDeserializer no longer exist. Compilation fails before this
 *   test can even run.
 *
 * Fix:
 *   JsonSerializer       → tools.jackson.databind.ser.ValueSerializer
 *   JsonDeserializer     → tools.jackson.databind.deser.ValueDeserializer
 *   SerializerProvider   → tools.jackson.databind.SerializationContext
 *   DeserializationContext stays named but moves to tools.jackson.databind
 */
@SpringBootTest
class JacksonClassRenamesTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void customSerializerShouldFormatPriceAsCurrency() throws Exception {
        // On Boot 3.5: passes — MoneySerializer formats 1099 as "10.99"
        // On Boot 4.0: compile error in MoneySerializer — cannot find symbol JsonSerializer
        Product product = new Product("Widget", 1099L);
        String json = objectMapper.writeValueAsString(product);
        assertTrue(json.contains("10.99"),
                "Expected price serialised as '10.99' but got: " + json);
    }

    @Test
    void customDeserializerShouldParseCurrencyToCents() throws Exception {
        // On Boot 3.5: passes — MoneyDeserializer parses "10.99" to 1099L
        // On Boot 4.0: compile error in MoneyDeserializer — cannot find symbol JsonDeserializer
        String json = "{\"name\":\"Widget\",\"priceCents\":\"10.99\"}";
        Product product = objectMapper.readValue(json, Product.class);
        assertEquals(1099L, product.getPriceCents(),
                "Expected 1099 cents but got: " + product.getPriceCents());
    }
}
