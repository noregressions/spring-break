package com.example;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;

/**
 * Category (a) — Won't Compile on 4.0
 *
 * A real JUnit 4 test using JUnit 4 annotations and assertions.
 *
 * Spring Boot 3.5 (JUnit Platform 1.x):
 *   junit-vintage-engine is BOM-managed and transitively provides junit:junit:4.13.2.
 *   org.junit.Test, org.junit.Assert, etc. are on the classpath.
 *   The vintage engine discovers and runs this test alongside JUnit 5 tests.
 *
 * Spring Boot 4.0 (JUnit Platform 2.x / JUnit 6):
 *   junit-vintage-engine is removed from the BOM. The junit:junit artifact
 *   is no longer on the classpath. This file fails to compile:
 *     error: package org.junit does not exist
 *
 * Fix: Migrate to JUnit 5 (Jupiter):
 *   - org.junit.Test          → org.junit.jupiter.api.Test
 *   - org.junit.Assert        → org.junit.jupiter.api.Assertions
 *   - org.junit.Before/After  → org.junit.jupiter.api.BeforeEach/AfterEach
 *   - @RunWith(SpringRunner.class) → @SpringBootTest (no runner needed)
 *   - @Rule TemporaryFolder   → @TempDir Path
 *
 * References:
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 * - JUnit 5 User Guide: https://junit.org/junit5/docs/current/user-guide/
 * - JUnit 5 Migration Tips: https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4
 */
public class LegacyJUnit4Test {

    private String message;

    @Before
    public void setUp() {
        // @Before is JUnit 4's equivalent of JUnit 5's @BeforeEach
        message = "Hello from JUnit 4";
    }

    @After
    public void tearDown() {
        // @After is JUnit 4's equivalent of JUnit 5's @AfterEach
        message = null;
    }

    @Test
    public void basicAssertion() {
        // org.junit.Assert is JUnit 4's assertion class.
        // Migrate to org.junit.jupiter.api.Assertions.
        Assert.assertEquals("JUnit 4 assertions work", 4, 2 + 2);
    }

    @Test
    public void stringAssertion() {
        Assert.assertNotNull("Message should not be null", message);
        Assert.assertTrue("Message should contain 'JUnit 4'",
                message.contains("JUnit 4"));
    }

    @Test
    public void collectionAssertion() {
        java.util.List<String> items = java.util.Arrays.asList("a", "b", "c");
        Assert.assertEquals("List should have 3 items", 3, items.size());
    }
}
