package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RetryServiceTest {
    @Autowired
    private RetryService retryService;

    @BeforeEach
    void setUp() {
        retryService.reset();
    }

    @Test
    void testRetrySemanticChange() {
        // BREAKING CHANGE: maxAttempts vs maxRetries semantic change
        //
        // In Spring Boot 3.5 with spring-retry 2.x:
        //   @Retryable(maxAttempts=3) → 3 total calls (1 initial + 2 retries)
        //
        // In Spring Boot 4.0 with spring-retry 3.x:
        //   @Retryable(maxRetries=3) → 4 total calls (1 initial + 3 retries)
        //
        // This is a Tier 3 "Wrong Results" breaking change: the code compiles and runs
        // without errors, but the invocation count differs silently. Affects rate limiting,
        // cloud API quotas, and idempotency guarantees.
        //
        // References:
        // - Spring Retry: https://github.com/spring-projects/spring-retry
        // - Spring Boot 4.0 Migration: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide

        try {
            retryService.unreliableOperation();
        } catch (Exception e) {
            // Expected: the method always fails and exhausts retries
            System.out.println("Method failed after exhausting retries: " + e.getMessage());
        }

        int invocations = retryService.getLastOperationInvocationCount();
        System.out.println("Total invocations: " + invocations);

        // This assertion passes on Spring Boot 3.5 (3 total calls)
        // but fails on Spring Boot 4.0 (4 total calls) with default maxRetries
        assertEquals(
                3,
                invocations,
                "Expected 3 total invocations (maxAttempts=3) but got " + invocations +
                        ". On Spring Boot 4.0, maxRetries changes the semantics: " +
                        "maxRetries=N means N+1 total calls, not N total calls."
        );
    }

    @Test
    void testRetryBehaviorExhaustion() {
        // Verify that the method is actually being retried
        assertThrows(Exception.class, () -> retryService.unreliableOperation());

        int invocations = retryService.getLastOperationInvocationCount();
        assertTrue(
                invocations > 1,
                "Expected multiple invocations due to retry, but got " + invocations
        );

        // On Spring Boot 3.5: invocations == 3
        // On Spring Boot 4.0: invocations == 4 (if using default maxRetries)
        System.out.println("✓ Retry mechanism triggered. Total attempts: " + invocations);
    }
}
