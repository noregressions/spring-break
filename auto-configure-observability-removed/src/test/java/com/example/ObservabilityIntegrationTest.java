package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tier 1 — compile break.
 *
 * Spring Boot 3.5.14:
 * - org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
 *   exists in spring-boot-test-autoconfigure.
 * - The annotation opts the test class out of Boot's default suppression of
 *   observability (spring-boot-test-autoconfigure ships an
 *   ObservabilityContextCustomizerFactory that injects a "test" PropertySource
 *   setting management.tracing.enabled=false during @SpringBootTest).
 * - Test classes that need tracing or metrics active during integration tests
 *   add this annotation to opt back in.
 *
 * Spring Boot 4.0.6:
 * - The annotation has been removed entirely from spring-boot-test-autoconfigure.
 *   It is not in any other Boot 4 jar — empirically verified by grepping
 *   every spring-boot-*-4.0.6.jar in the local Maven cache.
 * - The ObservabilityContextCustomizerFactory mechanism it opted out of has
 *   also been removed, so the suppression no longer happens by default.
 * - Code importing the annotation fails at javac before tests are compiled.
 *
 * Migration: delete the annotation. There is no replacement because the
 * underlying suppression mechanism was also removed. Tests that previously
 * needed @AutoConfigureObservability to enable tracing now get tracing wired
 * via the regular auto-configuration path (subject to the modular-actuator
 * split — see the spring-boot-actuator-autoconfigure-split test module for
 * that adjacent concern).
 */
@SpringBootTest
@AutoConfigureObservability
class ObservabilityIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // The fact that this test compiles and runs at all is the Tier 1 proof:
        // the @AutoConfigureObservability import resolved on this Boot version.
        // On Boot 4.0 the import fails and javac kills the build before this
        // test class is compiled.
        assertNotNull(context, "Application context should start when @AutoConfigureObservability resolves.");
    }
}
