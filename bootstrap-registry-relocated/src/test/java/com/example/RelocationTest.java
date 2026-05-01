package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.env.EnvironmentPostProcessor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/* Master list: 1.13 — BootstrapRegistry and EnvironmentPostProcessor package relocated. */
class RelocationTest {

    @Test
    void classesShouldExistInLegacyPackagesOnBoot35() {
        // Direct usage - fails to compile on Boot 4.0
        assertNotNull(BootstrapRegistryUsage.createRegistry());
        assertNotNull(new EnvironmentPostProcessorUsage());
    }

    @Test
    void bootstrapRegistryIsLoadableViaReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.boot.BootstrapRegistry"),
            "BootstrapRegistry should be in org.springframework.boot on Boot 3.5"
        );
    }

    @Test
    void environmentPostProcessorIsLoadableViaReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.boot.env.EnvironmentPostProcessor"),
            "EnvironmentPostProcessor should be in org.springframework.boot.env on Boot 3.5"
        );
    }
}
