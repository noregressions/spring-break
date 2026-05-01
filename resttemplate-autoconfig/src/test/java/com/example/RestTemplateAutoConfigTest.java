package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies that RestTemplateBuilder is auto-configured and ApiClient can be injected.
 *
 * Spring Boot 3.5: RestTemplateAutoConfiguration provides the builder — passes.
 * Spring Boot 4.0: Auto-config removed — startup fails with UnsatisfiedDependencyException.
 */
@SpringBootTest
class RestTemplateAutoConfigTest {

    @Autowired
    private ApiClient apiClient;

    @Test
    void restTemplateShouldBeAutoConfigured() {
        assertNotNull(apiClient.getRestTemplate(),
            "RestTemplate should be built from the auto-configured RestTemplateBuilder. "
                + "In Boot 4.0, RestTemplateAutoConfiguration is removed.");
    }
}
