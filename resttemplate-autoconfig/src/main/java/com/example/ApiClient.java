package com.example;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A service that depends on RestTemplateBuilder being auto-configured.
 *
 * Spring Boot 3.5: RestTemplateAutoConfiguration provides RestTemplateBuilder.
 * Spring Boot 4.0: Auto-config removed — UnsatisfiedDependencyException at startup.
 */
@Service
public class ApiClient {

    private final RestTemplate restTemplate;

    public ApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
