package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 2.5 — Jersey Jackson 2 required */
public class JerseyJacksonTest {

    @Test
    void jackson2ShouldBeOnClasspath() {
        // In Spring Boot 4.0, while Jackson 3 is the default, 
        // spring-boot-starter-jersey still relies on Jackson 2 because 
        // Jersey itself hasn't fully transitioned its official entity providers to Jackson 3.
        assertDoesNotThrow(
            () -> Class.forName("com.fasterxml.jackson.databind.ObjectMapper"),
            "Jackson 2 (com.fasterxml.jackson) should be on classpath when using Jersey starter in Boot 4.0"
        );
    }
}
