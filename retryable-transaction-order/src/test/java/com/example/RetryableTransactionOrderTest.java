package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * On Spring Boot 3.5: @Transactional wraps outside @Retryable. Failed attempts
 * roll back within the same transaction. After success, only 1 AuditLog entry
 * exists (attempt 3).
 *
 * On Spring Boot 4.0: @Retryable wraps outside @Transactional. Each retry gets
 * its own transaction. Failed attempts' saves are committed before the exception.
 * After success, 3 AuditLog entries exist (attempts 1, 2, 3). Test fails.
 */
@SpringBootTest
class RetryableTransactionOrderTest {

    @Autowired
    private RetryableService retryableService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        retryableService.resetCounter();
    }

    @Test
    void failedRetryAttemptsShouldBeRolledBack() {
        retryableService.processWithRetry("test-message");

        long count = auditLogRepository.count();
        assertEquals(1, count,
                "Expected only 1 AuditLog entry (failed attempts rolled back), but found " + count);
    }
}
