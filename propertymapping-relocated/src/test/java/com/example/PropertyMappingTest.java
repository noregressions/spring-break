package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.43 — @PropertyMapping relocated to org.springframework.boot.test.context */
class PropertyMappingTest {

    @Test
    void annotationIsPresent() {
        CustomTestAnnotation annotation = Usage.class.getAnnotation(CustomTestAnnotation.class);
        assertNotNull(annotation);
    }

    @Test
    void propertyMappingIsLoadableViaLegacyReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.boot.test.autoconfigure.properties.PropertyMapping"),
            "@PropertyMapping should be in org.springframework.boot.test.autoconfigure.properties on Boot 3.5"
        );
    }

    @CustomTestAnnotation("test")
    static class Usage {}
}
