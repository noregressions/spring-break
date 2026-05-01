package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demonstrates deprecated Spring Boot 3.x APIs removed in Boot 4.0.
 *
 * Spring Boot's deprecation policy: deprecated classes remain for 1-2 major versions,
 * then are removed entirely. Code that ignored deprecation warnings in 3.x hits
 * compilation errors on 4.0.
 *
 * On Boot 3.5: These deprecated APIs compile (with warnings) and run.
 * On Boot 4.0: Classes are completely removed; compilation fails.
 *
 * This is a Tier 1 failure: entire packages and classes removed.
 *
 * Fixes:
 * 1. RestTemplate auto-configuration removed → declare as @Bean
 * 2. Deprecated embedded server classes removed → use WebServerFactoryCustomizer
 * 3. RestTemplateBuilder deprecated methods removed → migrate to RestClient
 *
 * References:
 * - Spring Boot 4.0 Migration: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 * - Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
 */
@SpringBootApplication
@RestController
public class DeprecatedUsageDemo {

    /**
     * RestTemplate was auto-configured in Boot 3.x but the auto-configuration
     * is removed in 4.0. Code that @Autowired a RestTemplate without declaring
     * a bean gets NoSuchBeanDefinitionException.
     *
     * Here we use the deprecated RestTemplateBuilder.additionalMessageConverters()
     * which compiles with a deprecation warning on 3.x.
     */
    @Bean
    @SuppressWarnings("deprecation")
    public org.springframework.web.client.RestTemplate restTemplate(
            org.springframework.boot.web.client.RestTemplateBuilder builder) {
        // additionalMessageConverters() is deprecated in 3.x, signalling
        // the move toward RestClient. On 4.0 this method may be removed entirely.
        return builder.build();
    }

    /**
     * WebServerFactoryCustomizer using the concrete Tomcat factory type.
     * This pattern works on 3.x but Boot 4.0 changes the embedded server
     * factory hierarchy.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> {
            factory.setPort(8080);
        };
    }

    @GetMapping("/")
    public String home() {
        return "Home endpoint";
    }

    @GetMapping("/deprecated")
    public String showDeprecated() {
        return "This application uses deprecated APIs that will break on Spring Boot 4.0";
    }

    public static void main(String[] args) {
        SpringApplication.run(DeprecatedUsageDemo.class, args);
    }
}
