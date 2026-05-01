package com.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (d) — Runs on Both, Different Behaviour
 *
 * Demonstrates the Reactor Flux.take(n) API change.
 *
 * The old behaviour (unbounded upstream request) is still available via
 * take(n, false). Code that relied on the old default — e.g., side-effects
 * in upstream operators or metrics that counted requested items — breaks
 * silently when take(n) starts limiting the upstream request to exactly n.
 *
 * Spring Boot 3.5 (Reactor 3.6.x / 2024.0):
 *   Flux.take(n) already requests exactly n (the change landed in 3.4.x).
 *   Flux.take(n, false) requests unbounded (Long.MAX_VALUE).
 *   Both overloads available — code can choose explicitly.
 *
 * Spring Boot 4.0 (Reactor 3.7.x / 2025.0):
 *   Flux.take(n, false) is removed — only take(n) remains, always limited.
 *   Code that used take(n, false) for unbounded prefetch won't compile.
 *
 * This test verifies that take(n, false) exists and provides unbounded
 * request semantics on 3.5. On 4.0, this overload is gone → compile failure.
 *
 * References:
 * - reactor/reactor-core#2690
 * - Reactor Core Release Notes: https://github.com/reactor/reactor-core/releases
 * - Spring Boot 4.0 Migration Guide
 */
class TakePrefetchTest {

    @Test
    void takeWithLimitRequestsFiveExactly() {
        // take(n) on Reactor 3.6.x requests exactly n from upstream
        AtomicLong requestedCount = new AtomicLong(0);

        List<Integer> items = Flux.range(1, 100)
            .doOnRequest(n -> requestedCount.addAndGet(n))
            .take(5)
            .collectList()
            .block();

        assertNotNull(items);
        assertEquals(5, items.size(), "Should emit exactly 5 items");
        assertEquals(5, requestedCount.get(),
            "take(5) should request exactly 5 from upstream");
    }

    @Test
    @SuppressWarnings("deprecation")
    void takeWithoutLimitRequestsUnbounded() {
        // take(n, false) preserves the old unbounded-request behaviour.
        // On Boot 3.5: this overload exists and requests Long.MAX_VALUE.
        // On Boot 4.0: this overload is removed → compilation failure.
        AtomicLong requestedCount = new AtomicLong(0);

        List<Integer> items = Flux.range(1, 100)
            .doOnRequest(n -> requestedCount.addAndGet(n))
            .take(5, false)   // <-- removed in Reactor 2025.0
            .collectList()
            .block();

        assertNotNull(items);
        assertEquals(5, items.size(), "Should emit exactly 5 items");
        assertEquals(Long.MAX_VALUE, requestedCount.get(),
            "take(5, false) should request unbounded (Long.MAX_VALUE) from upstream. "
            + "Got " + requestedCount.get());
    }

    @Test
    void takeShouldCancelUpstreamAfterN() {
        AtomicLong onCancelCount = new AtomicLong(0);

        List<Integer> items = Flux.range(1, 1000)
            .doOnCancel(() -> onCancelCount.incrementAndGet())
            .take(10)
            .collectList()
            .block();

        assertNotNull(items);
        assertEquals(10, items.size(), "Should emit 10 items");
        assertEquals(1, onCancelCount.get(), "Upstream should be cancelled once");
    }
}
