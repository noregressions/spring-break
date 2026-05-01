package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Spring Boot REST application demonstrating Undertow removal in Boot 4.0.
 *
 * On Boot 3.5: This app starts with Undertow as the embedded servlet container
 *   (assuming pom.xml includes spring-boot-starter-undertow).
 *
 * On Boot 4.0: The build fails because spring-boot-starter-undertow no longer exists.
 *   Undertow support was completely removed. Migrate to Tomcat (default) or Jetty.
 *
 * This is a Tier 1 failure: artifact missing prevents build entirely.
 *
 * Fix: Remove the spring-boot-starter-undertow dependency from pom.xml.
 *      Keep spring-boot-starter-web (includes Tomcat by default).
 *      No code changes needed.
 *
 * References:
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 * - Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
 */
@SpringBootApplication
@RestController
public class UndertowApp {

    @GetMapping("/")
    public String home() {
        return "Hello from Undertow embedded server!";
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }

    public static void main(String[] args) {
        SpringApplication.run(UndertowApp.class, args);
    }
}
