package com.example;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RetryService {
    private final AtomicInteger invocationCount = new AtomicInteger(0);
    private final AtomicInteger lastOperationInvocations = new AtomicInteger(0);

    /**
     * A method decorated with @Retryable(maxAttempts=3).
     *
     * BREAKING CHANGE in Spring Boot 4.0 (Spring Retry 3.x):
     *
     * Spring Boot 3.5 with spring-retry 2.x:
     *   @Retryable(maxAttempts=3) → 3 total calls (1 initial + 2 retries)
     *
     * Spring Boot 4.0 with spring-retry 3.x:
     *   @Retryable(maxRetries=3) → 4 total calls (1 initial + 3 retries)
     *
     * This is a "silent" Tier 3 breaking change: the code compiles and runs,
     * but behavior differs unexpectedly. Affects rate limiting, costs, and idempotency.
     *
     * References:
     * - Spring Retry GitHub: https://github.com/spring-projects/spring-retry
     * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
     *
     * Fix: Replace @Retryable(maxAttempts=N) with @Retryable(maxRetries=N-1)
     */
    @Retryable(maxAttempts = 3)
    public void unreliableOperation() {
        int current = invocationCount.incrementAndGet();
        lastOperationInvocations.set(current);
        System.out.println("Attempt #" + current);

        // Always fail to trigger retries
        throw new RuntimeException("Simulated transient failure");
    }

    /**
     * Reset counters for testing.
     */
    public void reset() {
        invocationCount.set(0);
        lastOperationInvocations.set(0);
    }

    /**
     * Get the total number of invocations for the last operation.
     */
    public int getLastOperationInvocationCount() {
        return lastOperationInvocations.get();
    }

    /**
     * Get the total invocation count across all operations.
     */
    public int getTotalInvocationCount() {
        return invocationCount.get();
    }
}
