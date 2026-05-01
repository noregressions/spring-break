package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests Boot 4.0 rename of Jackson2ObjectMapperBuilderCustomizer to JsonMapperBuilderCustomizer.
 *
 * On Boot 3.5: the old class lives at
 *   org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
 *   in spring-boot-autoconfigure. Test compiles and passes.
 *
 * On Boot 4.0: that class is gone. The replacement is
 *   org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
 *   in the new spring-boot-jackson module. The import fails:
 *     "package org.springframework.boot.autoconfigure.jackson does not exist"
 *     "cannot find symbol: class Jackson2ObjectMapperBuilderCustomizer"
 *
 * Reference: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
class JacksonPropertyInclusionTest {

    @Test
    void jackson2CustomiserShouldExist() {
        Jackson2ObjectMapperBuilderCustomizer customiser =
                builder -> builder.simpleDateFormat("yyyy-MM-dd");
        assertNotNull(customiser,
                "Jackson2ObjectMapperBuilderCustomizer should be available on Boot 3.5. " +
                "On Boot 4.0, this class is renamed to JsonMapperBuilderCustomizer and " +
                "moved to org.springframework.boot.jackson.autoconfigure (new artifact " +
                "spring-boot-jackson).");
    }
}
