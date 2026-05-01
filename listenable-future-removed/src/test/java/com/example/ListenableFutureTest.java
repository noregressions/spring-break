package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.util.concurrent.ListenableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests ListenableFuture removal between Boot versions.
 *
 * Spring Boot 3.5 (Spring Framework 6.1):
 * - ListenableFuture and SettableListenableFuture exist in spring-core
 * - AsyncService compiles and returns a completed future
 * - Tests run and pass
 *
 * Spring Boot 4.0 (Spring Framework 7.0):
 * - ListenableFuture, SettableListenableFuture, ListenableFutureCallback all deleted
 * - AsyncService fails to compile — class not found
 * - Build fails at compilation phase
 *
 * This is a Tier 1 failure: build fails before tests run.
 *
 * Fix: Replace ListenableFuture with CompletableFuture.
 *
 * References:
 * - Spring Framework 7 Migration: https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-7.x
 */
class ListenableFutureTest {

    @Test
    void fetchDataReturnsExpectedValue() throws Exception {
        AsyncService service = new AsyncService();
        ListenableFuture<String> future = service.fetchData();
        assertEquals("data-from-service", future.get(),
                "fetchData() should return 'data-from-service'");
    }

    @Test
    void listenableFutureClassIsAvailable() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.util.concurrent.ListenableFuture"),
            "ListenableFuture should be on classpath with Boot 3.x. " +
            "On Boot 4.0, this class is removed — use CompletableFuture instead."
        );
    }
}
