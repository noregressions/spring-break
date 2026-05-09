package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tier 1 — compile break.
 *
 * Spring Boot 3.5.14:
 * - org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing
 *   exists. UserTracingExtension.java compiles successfully. The Spring application
 *   context starts; this test runs and passes.
 *
 * Spring Boot 4.0.6:
 * - The annotation has been renamed to ConditionalOnEnabledTracingExport (paired
 *   with the property rename to management.tracing.export.enabled — same change).
 * - UserTracingExtension's import does not resolve.
 * - javac fails: "package org.springframework.boot.actuate.autoconfigure.tracing
 *   does not contain ConditionalOnEnabledTracing" or similar.
 * - The build stops at compile; this test never runs. Tier 1 break.
 *
 * What this test deliberately does NOT do:
 * - It does not assert that the @ConditionalOnEnabledTracing condition fires at
 *   runtime. Spring Boot test infrastructure injects a synthetic property source
 *   that defaults management.tracing.enabled=false to suppress observability noise
 *   during test runs, so the conditional bean is not created in test context
 *   regardless of application.properties. Asserting on the conditional firing
 *   would test Boot's test defaults, not the rename. This module's purpose is
 *   purely the COMPILE break: if the user code compiles, Boot version is 3.x; if
 *   javac fails on the import, Boot version is 4.x.
 *
 * The break primarily affects starter / library authors and corporate platform teams
 * that wrote @ConditionalOnEnabledTracing-gated configurations of their own. Most
 * application code does not reference this annotation directly.
 *
 * Fix: rename the import and annotation:
 *   - org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing
 *   + org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracingExport
 *
 *   - @ConditionalOnEnabledTracing
 *   + @ConditionalOnEnabledTracingExport
 *
 * If you also set management.tracing.enabled in application.properties, rename it
 * to management.tracing.export.enabled — see the sibling test module
 * tracing-export-property-renamed for the silent regression that property rename
 * causes on its own.
 */
@SpringBootTest
class ConditionalOnEnabledTracingRenamedTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // The fact that this test compiles and runs at all is the Tier 1 proof:
        // UserTracingExtension's @ConditionalOnEnabledTracing import resolved.
        // On Boot 4.0 the import fails and javac kills the build before this
        // test class is compiled.
        assertNotNull(context, "Application context should start when UserTracingExtension compiles.");
    }
}
