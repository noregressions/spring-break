package com.example;

import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * Direct usage of TestRestTemplate using the Spring Boot 3.5 package path.
 */
public class TestRestTemplateUsage {

    public static TestRestTemplate createInstance() {
        return new TestRestTemplate();
    }
}
