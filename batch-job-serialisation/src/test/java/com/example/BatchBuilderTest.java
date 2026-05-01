package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Category (c) — Runtime Errors on 4.0
 *
 * Demonstrates Spring Batch 6.0 removal of deprecated listener support classes.
 *
 * Spring Batch 5.2 (Spring Boot 3.5):
 *   - JobExecutionListenerSupport exists (deprecated since 5.0)
 *   - StepExecutionListenerSupport exists (deprecated since 5.0)
 *   - ChunkListenerSupport exists (deprecated since 5.0)
 *   These are convenience base classes for implementing batch listeners
 *   with empty default methods.
 *
 * Spring Batch 6.0 (Spring Boot 4.0):
 *   - All three classes removed entirely
 *   - Code referencing them throws ClassNotFoundException at runtime
 *
 * Fix: Implement the listener interfaces directly instead of extending
 * the support classes. Since Java 8, interfaces can have default methods,
 * making base classes unnecessary.
 *
 * Reference: https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide
 */
public class BatchBuilderTest {

    @Test
    void jobExecutionListenerSupportShouldExist() {
        // Spring Batch 5.2: deprecated since 5.0, still present — passes
        // Spring Batch 6.0: removed — ClassNotFoundException
        assertDoesNotThrow(
            () -> Class.forName(
                "org.springframework.batch.core.listener.JobExecutionListenerSupport"),
            "JobExecutionListenerSupport should exist on Spring Batch 5.2 (deprecated since 5.0). " +
            "Removed in Batch 6.0. Implement JobExecutionListener directly instead."
        );
    }

    @Test
    void stepExecutionListenerSupportShouldExist() {
        // Spring Batch 5.2: deprecated since 5.0, still present — passes
        // Spring Batch 6.0: removed — ClassNotFoundException
        assertDoesNotThrow(
            () -> Class.forName(
                "org.springframework.batch.core.listener.StepExecutionListenerSupport"),
            "StepExecutionListenerSupport should exist on Spring Batch 5.2 (deprecated since 5.0). " +
            "Removed in Batch 6.0. Implement StepExecutionListener directly instead."
        );
    }

    @Test
    void chunkListenerSupportShouldExist() {
        // Spring Batch 5.2: deprecated since 5.0, still present — passes
        // Spring Batch 6.0: removed — ClassNotFoundException
        assertDoesNotThrow(
            () -> Class.forName(
                "org.springframework.batch.core.listener.ChunkListenerSupport"),
            "ChunkListenerSupport should exist on Spring Batch 5.2 (deprecated since 5.0). " +
            "Removed in Batch 6.0. Implement ChunkListener directly instead."
        );
    }
}
