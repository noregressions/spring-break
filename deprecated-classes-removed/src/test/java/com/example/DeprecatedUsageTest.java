package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies that the deprecated API usage produces working beans.
 *
 * On Spring Boot 3.x: RestTemplate is auto-configured and our bean is created.
 *   Tests pass.
 *
 * On Spring Boot 4.0: RestTemplate auto-configuration is removed. If our
 *   @Bean method uses removed deprecated APIs, compilation fails first.
 *   If the bean declaration survives but the auto-config wiring changes,
 *   this test catches it with NoSuchBeanDefinitionException.
 */
@SpringBootTest
class DeprecatedUsageTest {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void restTemplateBeanExists() {
        // On 3.x: passes — RestTemplate bean created via our deprecated builder pattern
        // On 4.0: fails — either compilation error (if deprecated method removed)
        //         or NoSuchBeanDefinitionException (if auto-config changed)
        assertNotNull(restTemplate, "RestTemplate bean should be available");
    }
}
