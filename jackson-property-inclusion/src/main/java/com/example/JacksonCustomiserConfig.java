package com.example;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demonstrates the Boot 4.0 rename of Jackson2ObjectMapperBuilderCustomizer
 * to JsonMapperBuilderCustomizer.
 *
 * On Spring Boot 3.5:
 *   org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
 *   exists in spring-boot-autoconfigure. Apps that customise the auto-configured
 *   ObjectMapper register a @Bean of this type.
 *
 * On Spring Boot 4.0:
 *   The Jackson 2 customiser is gone. The replacement is
 *   org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
 *   in the new spring-boot-jackson module — a different package and a different
 *   artifact. Both the import and the @Bean type fail to compile.
 *
 * This is a Tier 1 (Won't Compile) failure.
 *
 * Reference: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
@Configuration
public class JacksonCustomiserConfig {

    /**
     * The pre-4.0 way to customise the auto-configured Jackson ObjectMapper.
     * Boot 4.0 expects a JsonMapperBuilderCustomizer in a different package
     * — code referencing the old type breaks.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer excludeNullsCustomiser() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
