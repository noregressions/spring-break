package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration that creates a RestTemplate backed by OkHttp3.
 *
 * Spring Boot 3.5: OkHttp3ClientHttpRequestFactory available in spring-web.
 * Spring Boot 4.0: OkHttp3ClientHttpRequestFactory removed, compilation fails.
 *
 * Fix: Use JdkClientHttpRequestFactory, or add a third-party OkHttp adapter.
 */
@Configuration
public class OkHttpConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
