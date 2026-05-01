package com.example;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (a) — Won't Compile on 4.0
 *
 * Demonstrates Testcontainers class relocation between TC 1.x and 2.x.
 *
 * Testcontainers 1.x (Boot 3.5):
 *   PostgreSQLContainer lives in org.testcontainers.containers package.
 *   import org.testcontainers.containers.PostgreSQLContainer — compiles.
 *
 * Testcontainers 2.x (Boot 4.0):
 *   PostgreSQLContainer relocated to org.testcontainers.postgresql package.
 *   import org.testcontainers.containers.PostgreSQLContainer — compile error:
 *     "cannot find symbol"
 *
 * On Boot 3.5: compiles and passes.
 * On Boot 4.0:   fails to compile — import path changed.
 *
 * Fix: Update imports to the new package structure:
 *   import org.testcontainers.postgresql.PostgreSQLContainer;
 *   import org.testcontainers.mysql.MySQLContainer;
 *   etc.
 *
 * GenericContainer stays at org.testcontainers.containers.GenericContainer
 * on both versions — only the database-specific containers moved.
 *
 * References:
 * - Testcontainers 2.0 Migration: https://testcontainers.com/guides/testcontainers-2-migration/
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
class ContainerRelocationTest {

    @Test
    void postgresContainerClassIsAvailable() {
        // On TC 1.x: org.testcontainers.containers.PostgreSQLContainer exists — passes
        // On TC 2.x: class moved to org.testcontainers.postgresql — compile error
        assertNotNull(PostgreSQLContainer.class,
                "PostgreSQLContainer should be in org.testcontainers.containers on TC 1.x");
    }

    @Test
    void genericContainerClassIsAvailable() {
        // GenericContainer stays in org.testcontainers.containers on both versions
        assertNotNull(GenericContainer.class,
                "GenericContainer should be available on all Testcontainers versions");
    }
}
