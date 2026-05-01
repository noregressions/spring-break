package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests OkHttp3ClientHttpRequestFactory removal between Boot versions.
 *
 * Spring Boot 3.5 (Spring Framework 6.1):
 * - OkHttp3ClientHttpRequestFactory available in spring-web
 * - OkHttpConfig compiles, RestTemplate bean is created with OkHttp3 factory
 * - Tests run and pass
 *
 * Spring Boot 4.0 (Spring Framework 7.0):
 * - OkHttp3ClientHttpRequestFactory removed from spring-web
 * - OkHttpConfig fails to compile — class not found
 * - Build fails at compilation phase
 *
 * This is a Tier 1 failure: build fails before tests run.
 *
 * Fix: Use JdkClientHttpRequestFactory or another HTTP client factory.
 *
 * References:
 * - Spring Framework 7 Migration: https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-7.x
 */
@SpringBootTest
class OkHttp3RemovedTest {

    @SpringBootApplication
    static class TestApp {
    }

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void restTemplateBeanIsNotNull() {
        assertNotNull(restTemplate, "RestTemplate bean should be created by OkHttpConfig");
    }

    @Test
    void restTemplateUsesOkHttp3Factory() {
        assertInstanceOf(OkHttp3ClientHttpRequestFactory.class,
                restTemplate.getRequestFactory(),
                "RestTemplate should use OkHttp3ClientHttpRequestFactory");
    }
}
