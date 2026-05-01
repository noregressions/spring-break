package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Master list: 1.40 — TestRestTemplate package relocated.
 */
class TestRestTemplateRemovedTest {

    @Test
    void testRestTemplateShouldExistOnBoot35() {
        // Direct use — fails to compile on Boot 4.0.
        TestRestTemplate template = TestRestTemplateUsage.createInstance();
        assertNotNull(template);
    }

    @Test
    void testRestTemplateIsLoadableViaReflection() {
        // Even if the import resolves (which it shouldn't on 4.0), ensure the class loads.
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.boot.test.web.client.TestRestTemplate"),
            "TestRestTemplate should be in org.springframework.boot.test.web.client on Boot 3.5"
        );
    }
}
