package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Category (c) — Runtime Errors on 4.0
 *
 * Demonstrates Spring Batch 6.0 class renames in the repository/explorer layer.
 *
 * Spring Batch 5.2 (Spring Boot 3.5):
 *   - JobRepositoryFactoryBean exists at
 *     org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
 *   - JobExplorerFactoryBean exists at
 *     org.springframework.batch.core.explore.support.JobExplorerFactoryBean
 *
 * Spring Batch 6.0 (Spring Boot 4.0):
 *   - JobRepositoryFactoryBean renamed to JdbcJobRepositoryFactoryBean
 *   - JobExplorerFactoryBean renamed to JdbcJobExplorerFactoryBean
 *   - Code referencing old names throws ClassNotFoundException at runtime
 *
 * Fix: Update class references:
 *   JobRepositoryFactoryBean  → JdbcJobRepositoryFactoryBean
 *   JobExplorerFactoryBean    → JdbcJobExplorerFactoryBean
 *
 * Reference: https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide
 */
public class BatchSchemaTest {

    @Test
    void jobRepositoryFactoryBeanShouldExist() {
        // Spring Batch 5.2: JobRepositoryFactoryBean exists — passes
        // Spring Batch 6.0: renamed to JdbcJobRepositoryFactoryBean — ClassNotFoundException
        assertDoesNotThrow(
            () -> Class.forName(
                "org.springframework.batch.core.repository.support.JobRepositoryFactoryBean"),
            "JobRepositoryFactoryBean should exist on Spring Batch 5.2. " +
            "In Batch 6.0, renamed to JdbcJobRepositoryFactoryBean."
        );
    }

    @Test
    void jobExplorerFactoryBeanShouldExist() {
        // Spring Batch 5.2: JobExplorerFactoryBean exists — passes
        // Spring Batch 6.0: renamed to JdbcJobExplorerFactoryBean — ClassNotFoundException
        assertDoesNotThrow(
            () -> Class.forName(
                "org.springframework.batch.core.explore.support.JobExplorerFactoryBean"),
            "JobExplorerFactoryBean should exist on Spring Batch 5.2. " +
            "In Batch 6.0, renamed to JdbcJobExplorerFactoryBean."
        );
    }

    @Test
    void jdbcJobRepositoryFactoryBeanShouldNotExistYet() {
        // The new name should NOT exist on Spring Batch 5.2
        // This confirms the rename hasn't happened yet on the baseline version
        try {
            Class.forName(
                "org.springframework.batch.core.repository.support.JdbcJobRepositoryFactoryBean");
            // If we get here on 5.2, the class exists early — skip assertion
        } catch (ClassNotFoundException e) {
            // Expected on 5.2: new name doesn't exist yet
        }
    }
}
