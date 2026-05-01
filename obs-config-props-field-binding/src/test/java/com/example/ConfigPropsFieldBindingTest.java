package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Category (a) — Won't Compile on 4.0
 *
 * Verifies that constructor-bound @ConfigurationProperties work when the
 * deprecated type-level @ConstructorBinding annotation is used.
 *
 * Spring Boot 3.5: annotation exists (deprecated) — compiles and binds.
 * Spring Boot 4.0: annotation removed — compilation failure in AppConfig.java.
 */
@SpringBootTest(classes = FieldBindingApp.class)
class ConfigPropsFieldBindingTest {

    @Autowired
    private AppConfig config;

    @Test
    void constructorBindingShouldPopulateProperties() {
        assertEquals("example.com", config.getHost());
        assertEquals(9090, config.getPort());
        assertTrue(config.isDebug());
    }
}
