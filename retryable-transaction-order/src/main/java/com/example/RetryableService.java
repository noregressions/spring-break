package com.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetryableService {

    private final AuditLogRepository auditLogRepository;
    private final AtomicInteger attemptCounter = new AtomicInteger(0);

    public RetryableService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Retryable(maxAttempts = 3)
    @Transactional
    public void processWithRetry(String message) {
        int attempt = attemptCounter.incrementAndGet();
        auditLogRepository.save(new AuditLog(message, attempt));
        if (attempt < 3) {
            throw new RuntimeException("Simulated failure on attempt " + attempt);
        }
    }

    /** Reset the attempt counter between tests. */
    public void resetCounter() {
        attemptCounter.set(0);
    }
}
