package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A simple auto-configuration that creates a marker bean.
 * Registered via META-INF/spring.factories (old-style).
 *
 * Spring Boot 3.5: spring.factories is still honoured for auto-configuration.
 * Spring Boot 4.0: spring.factories is ignored; only AutoConfiguration.imports works.
 */
@Configuration
public class TestAutoConfig {

    @Bean
    public String testMarker() {
        return "auto-configured";
    }
}
