package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests spring-jcl bridge removal between Boot versions.
 *
 * Spring Boot 3.5 (Spring Framework 6.1):
 * - spring-jcl is a module within spring-framework, managed by the BOM
 * - Declaring spring-jcl without version works (BOM provides version)
 * - LoggingService compiles and runs with commons-logging via spring-jcl bridge
 * - Tests run and pass
 *
 * Spring Boot 4.0 (Spring Framework 7.0):
 * - spring-jcl module removed from Spring Framework entirely
 * - Declaring spring-jcl without version fails to resolve (removed from BOM)
 * - Build fails at Maven dependency resolution phase
 * - Tests never run; build stops with: "Could not find artifact"
 *
 * This is a Tier 1 failure: build fails before compilation.
 *
 * Fix: Remove the explicit spring-jcl dependency. If you need commons-logging APIs,
 *      they are still available via the commons-logging 1.3.0 artifact that
 *      Spring Framework 7 depends on directly.
 *
 * References:
 * - Spring Framework 7 Migration: https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-7.x
 */
class SpringJclRemovedTest {

    @Test
    void loggingServiceReturnsProcessedValue() {
        LoggingService service = new LoggingService();
        String result = service.doWork("hello");
        assertEquals("processed-hello", result,
                "doWork('hello') should return 'processed-hello'");
    }

    @Test
    void springJclIsOnClasspath() {
        // spring-jcl repackages commons-logging classes under org.apache.commons.logging
        // On Boot 3.x: spring-jcl resolves, this class loads fine
        // On Boot 4.0: spring-jcl artifact doesn't exist, dependency resolution fails
        //              before this test can even run
        assertDoesNotThrow(
            () -> Class.forName("org.apache.commons.logging.LogFactory"),
            "LogFactory should be available via spring-jcl on Boot 3.x. " +
            "On Boot 4.0, spring-jcl is removed — use commons-logging 1.3.0 directly."
        );
    }
}
