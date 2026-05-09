package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the silent relocation of BraveAutoConfiguration between Boot versions.
 *
 * Spring Boot 3.5.14:
 * - spring-boot-actuator-autoconfigure ships
 *   org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration.
 * - spring-boot-starter-actuator pulls actuator-autoconfigure in transitively.
 * - With micrometer-tracing-bridge-brave on the classpath, tracing wires up
 *   automatically — no extra dependency needed. The test below passes.
 *
 * Spring Boot 4.0.6:
 * - spring-boot-actuator-autoconfigure has been split per-concern. The tracing
 *   piece moved to a new module: spring-boot-micrometer-tracing-brave, with the
 *   class also moved to a new package:
 *   org.springframework.boot.micrometer.tracing.brave.autoconfigure.BraveAutoConfiguration.
 * - spring-boot-starter-actuator no longer pulls the tracing auto-config in.
 *   The user must add spring-boot-micrometer-tracing-brave explicitly.
 * - Without it, neither the old nor the new BraveAutoConfiguration class is on
 *   the classpath. The test below fails: tracing is silently not wired.
 *
 * This is a Tier 3 silent regression in the user-visible sense (no startup
 * error, no warning, just no traces flowing) — and a Tier 1 compile break for
 * any code that imports the old or new BraveAutoConfiguration directly.
 *
 * Fix: on Boot 4, add the new module:
 *
 *   <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-micrometer-tracing-brave</artifactId>
 *   </dependency>
 *
 * (or spring-boot-micrometer-tracing-opentelemetry, depending on which
 * tracer you use). Empirically verified: with that dependency added on Boot 4,
 * the new-package BraveAutoConfiguration is on the classpath and tracing wires
 * up as expected.
 */
@SpringBootTest
class TracingAutoConfigRelocatedTest {

    private static final String BOOT_3_BRAVE_AUTOCONFIG =
        "org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration";
    private static final String BOOT_4_BRAVE_AUTOCONFIG =
        "org.springframework.boot.micrometer.tracing.brave.autoconfigure.BraveAutoConfiguration";

    @Test
    void braveAutoConfigClassReachableViaStarter() {
        // Either Boot version's BraveAutoConfiguration should be on the classpath
        // when the user has spring-boot-starter-actuator + micrometer-tracing-bridge-brave.
        // On 3.5 the class ships transitively via spring-boot-actuator-autoconfigure.
        // On 4.0 neither is reachable unless spring-boot-micrometer-tracing-brave is
        // added explicitly — the actuator starter no longer brings it in.
        boolean oldPath = isLoadable(BOOT_3_BRAVE_AUTOCONFIG);
        boolean newPath = isLoadable(BOOT_4_BRAVE_AUTOCONFIG);

        assertTrue(
            oldPath || newPath,
            "BraveAutoConfiguration should be reachable on the classpath. " +
            "On Boot 3 it's in spring-boot-actuator-autoconfigure (pulled in by " +
            "spring-boot-starter-actuator). On Boot 4 it moved to " +
            "spring-boot-micrometer-tracing-brave — that module must be added " +
            "explicitly. Found: oldPath=" + oldPath + " newPath=" + newPath
        );
    }

    private static boolean isLoadable(String fqcn) {
        try {
            Class.forName(fqcn);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
