package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tier 1 — compile break.
 *
 * Boot 3.5.14: CustomSerializerMarker compiles (uses Boot 3's @JsonComponent
 * at org.springframework.boot.jackson.JsonComponent). This test runs and passes.
 *
 * Boot 4.0.6: CustomSerializerMarker fails to compile because the annotation
 * was renamed to @JacksonComponent. The test class is never compiled or run;
 * the build dies in javac.
 *
 * The test itself does nothing more than confirm the marker class loaded.
 * The compile success on 3.5 and compile failure on 4.0 are the entire proof.
 */
class JacksonComponentRenameTest {

    @Test
    void markerClassExists() {
        assertNotNull(CustomSerializerMarker.class,
            "CustomSerializerMarker should be loadable on Boot 3.x where @JsonComponent resolves.");
    }
}
