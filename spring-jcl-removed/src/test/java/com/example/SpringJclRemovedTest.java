package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests spring-jcl bridge removal between Boot versions.
 *
 * Spring Boot 3.5.14 (Spring Framework 6.2):
 * - spring-jcl is a module within Spring Framework, managed by the Boot BOM
 * - Declaring spring-jcl without a version works (BOM provides it)
 * - LoggingService compiles and runs; commons-logging API routes through spring-jcl
 * - Tests run and pass
 *
 * Spring Boot 4.0.6 (Spring Framework 7.0):
 * - spring-jcl module removed from Spring Framework entirely
 * - Declaring spring-jcl without a version fails to resolve (no longer in BOM)
 * - Build fails at Maven dependency resolution phase with "Could not find artifact"
 * - Tests never run
 *
 * This is a Tier 1 failure: dependency resolution fails before compilation.
 *
 * What this test proves (the proven path in the cheat sheet):
 *   Declaring spring-jcl explicitly works on 3.5 and breaks on 4.0.
 *   Removing the declaration unblocks the 4.0 build.
 *
 * What this test does NOT prove (the cheat sheet flags this in Watch Out):
 *   - Whether removing jcl-over-slf4j is safe in projects that currently rely on it
 *   - Whether removing legacy commons-logging exclusions on spring-core is safe
 *   - Whether Commons Logging 1.3.x reliably routes through SLF4J in any given project's
 *     classpath layout. Those are project-specific and require live verification of the
 *     post-migration logging output.
 *
 * Fix: Remove the explicit spring-jcl declaration. Commons Logging (1.3.x, managed by the
 *      Boot 4.0 BOM) is pulled in transitively via spring-core; commons-logging APIs continue
 *      to work without further changes.
 *
 * References:
 * - Spring Framework 7 upgrade notes:
 *   https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-7.x
 * - Spring Boot 4.0 Migration Guide:
 *   https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
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
