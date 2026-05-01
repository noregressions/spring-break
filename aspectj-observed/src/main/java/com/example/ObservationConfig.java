package com.example;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers ObservedAspect bean.
 *
 * Spring Boot 3.5: this configuration combined with auto-config makes @Observed work.
 * Spring Boot 4.0: auto-config changes mean the AspectJ weaver must be explicitly configured.
 */
@Configuration
public class ObservationConfig {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }
}
