package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.retry.annotation.Retryable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Spring Retry BOM removal between Boot versions.
 *
 * Spring Boot 3.5 (spring-retry in BOM):
 * - spring-retry is managed dependency in the BOM
 * - Declaring spring-retry without version works (BOM provides 2.0.x)
 * - @Retryable annotation is available on the classpath
 * - Tests run and pass
 *
 * Spring Boot 4.0 (spring-retry removed from BOM):
 * - spring-retry removed from the dependency management BOM
 * - Declaring spring-retry without version fails to resolve
 * - Build fails at Maven dependency resolution phase
 * - Tests never run; build stops with: "Could not find artifact"
 *
 * This is a Tier 1 failure: build fails before compilation.
 *
 * Fix: Either add explicit version to pom.xml (not recommended) or remove
 *      spring-retry dependency and use Spring Framework 7's built-in retry.
 *
 * References:
 * - Spring Boot 4.0 Migration: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 * - Spring Framework 7: https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-7.x
 * - Spring Retry: https://github.com/spring-projects/spring-retry
 */
class SpringRetryRemovedTest {

    @Test
    void springRetryAnnotationIsAvailable() {
        // This test only executes if spring-retry resolved successfully.
        // On Boot 3.x: spring-retry is BOM-managed → dependency resolves → test runs
        // On Boot 4.0: spring-retry removed from BOM → dependency resolution fails → build stops
        assertNotNull(Retryable.class, "Retryable annotation should be available");
        assertTrue(Retryable.class.isAnnotation(), "Retryable should be an annotation type");
    }

    @Test
    void retryableCanBeLoadedByReflection() {
        // Alternative verification using reflection
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.retry.annotation.Retryable"),
            "org.springframework.retry.annotation.Retryable should be loadable on Boot 3.x. " +
            "On Boot 4.0, spring-retry is removed — use Spring Framework 7's core retry instead."
        );
    }
}
