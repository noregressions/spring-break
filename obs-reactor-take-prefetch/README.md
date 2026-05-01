# Reactor Flux.take(n) Prefetch Semantics Change Migration Test

## One-Line Summary
Reactor 2025.0 (Spring Boot 4.0) changes `Flux.take(n)` to request exactly n items instead of unbounded, silently affecting backpressure, resource consumption, and metrics.

## What Breaks

Reactor 2025.0 bundled with Spring Boot 4.0 changed how `Flux.take(n)` requests items from upstream. The API signature remains identical, but the **backpressure semantics change silently**:

**Spring Boot 3.4.1 (Reactor 2024.0):** `take(n)` requests `Long.MAX_VALUE` (unbounded) from upstream
**Spring Boot 4.0 (Reactor 2025.0):** `take(n)` requests exactly `n` items from upstream

This is a **silent change with no compilation or runtime errors**. The first n items are emitted correctly on both versions, so basic tests pass. However, the upstream source receives different demand signals, affecting:
- Backpressure semantics (upstream doesn't know how many items the final subscriber wants)
- Resource consumption (memory, CPU, file handles opened by upstream)
- Metrics and monitoring (demand counts differ significantly)
- Cache/buffer strategy decisions in upstream operators
- Timer and interval operators (scheduling differs)

## How This Test Works

The test suite uses Reactor's `doOnRequest()` operator to track how many items `take(n)` requests from upstream:

1. **takeShouldRequestUnboundedOnReactor2024()**: Creates a `Flux.range()` with `doOnRequest()` tracking, applies `take(5)`, and asserts the requested count equals `Long.MAX_VALUE` (Reactor 2024.0 behavior)
2. **takeShouldYieldExactlyFiveItems()**: Verifies both versions emit exactly 5 items (backward compatible)
3. **takeShouldCancelUpstreamAfterN()**: Verifies both versions cancel upstream after n items (backward compatible)

The first test reveals the breaking change by checking the total request count. The other tests pass on both versions, creating a false sense of compatibility.

## On Spring Boot 3.4.1 (Reactor 2024.0)

```bash
mvn test
```

All three tests pass. Example output:
```
✓ takeShouldRequestUnboundedOnReactor2024
  Expected unbounded request (Long.MAX_VALUE) from take(5)
  Requested: 9223372036854775807

✓ takeShouldYieldExactlyFiveItems
  Should emit exactly 5 items — PASS

✓ takeShouldCancelUpstreamAfterN
  Upstream should be cancelled once — PASS
```

## On Spring Boot 4.0 (Reactor 2025.0)

```bash
mvn test
```

First test fails. Example failure:
```
✗ takeShouldRequestUnboundedOnReactor2024
  Expected unbounded request (Long.MAX_VALUE) from take(5), but got 5
  Reactor 2025.0 changed take(n) to request exactly n.

✓ takeShouldYieldExactlyFiveItems
  Should emit exactly 5 items — PASS

✓ takeShouldCancelUpstreamAfterN
  Upstream should be cancelled once — PASS
```

## Real-World Impact

### Scenario 1: Expensive Data Generation

```java
// Generates expensive objects on demand
Flux<ExpensiveData> source = Flux.create(sink -> {
    for (int i = 0; i < 1000; i++) {
        sink.next(new ExpensiveData(i));
    }
});

Flux<ExpensiveData> limited = source.take(10);
```

**Reactor 2024.0**: Generates all 1000 objects, take() discards 990 → wasteful
**Reactor 2025.0**: Generates only 10 objects → efficient backpressure

### Scenario 2: Database Cursors

```java
Flux<Row> rows = Flux.fromIterable(cursorResults)
    .doOnRequest(n -> logger.info("DB cursor demand: {}", n))
    .take(100);
```

**Reactor 2024.0**: `doOnRequest(Long.MAX_VALUE)` → all rows fetched into memory
**Reactor 2025.0**: `doOnRequest(100)` → only 100 rows fetched

### Scenario 3: Monitoring Dashboards

```java
flux.doOnRequest(n -> metrics.increment("requests", n))
    .take(limit)
    .subscribe(item -> process(item));
```

**Reactor 2024.0**: Metrics show `Long.MAX_VALUE` requests
**Reactor 2025.0**: Metrics show actual `limit` requests

Dashboards and alerting rules based on these numbers break or behave unexpectedly.

### Scenario 4: Timer/Interval Operators

```java
Flux<Long> ticks = Flux.interval(Duration.ofMillis(100))
    .take(5)
    .doOnRequest(n -> logger.info("Interval demand: {}", n));
```

**Reactor 2024.0**: Timer is created for unbounded emissions, take() stops after 5
**Reactor 2025.0**: Timer is created for exactly 5 emissions (saves resources)

## Fix / Migration Path

### Option 1: Accept the New Semantics (Best Practice)

The new semantics align better with reactive principles. Update your code and tests to expect bounded requests:

```java
// Before: assumed unbounded requests
AtomicLong demanded = new AtomicLong(0);
flux.doOnRequest(n -> demanded.addAndGet(n))
    .take(limit)
    .subscribe();
assertEquals(Long.MAX_VALUE, demanded.get());

// After: expect bounded requests
flux.doOnRequest(n -> demanded.addAndGet(n))
    .take(limit)
    .subscribe();
// On Reactor 2025.0: demanded will be around `limit`
assertTrue(demanded.get() <= limit + 1);  // Allow small overhead
```

### Option 2: Restore Unbounded Requests (Compatibility)

If you need unbounded requests from upstream, explicitly request them:

```java
// Explicitly request unbounded after take()
flux.take(limit)
    .doOnSubscribe(subscription -> subscription.request(Long.MAX_VALUE))
    .subscribe();
```

### Option 3: Track Demand in Tests

Update tests to verify demand handling rather than asserting exact values:

```java
@Test
void verifyBackpressureBehavior() {
    AtomicLong requested = new AtomicLong(0);
    
    Flux<Integer> source = Flux.range(1, 100)
        .doOnRequest(n -> requested.addAndGet(n));
    
    StepVerifier.create(source.take(10))
        .expectNextCount(10)
        .verifyComplete();
    
    // Verify upstream received a reasonable demand
    // Exact value varies by Reactor version
    assertTrue(
        requested.get() >= 10,
        "Upstream should receive at least 10 items of demand"
    );
}
```

### Option 4: Audit and Adjust Upstream Logic

Review how upstream operators handle demand:

```java
// BEFORE: Unsafe assumptions about unbounded requests
Flux<Data> cache = sourceData.cache();  // No size limit
Flux<Data> limited = cache.take(10);    // Assumes cache() is safe

// AFTER: Explicit buffer/cache sizes
Flux<Data> cache = sourceData.cache(100);  // Explicit size
Flux<Data> limited = cache.take(10);       // Clear contract
```

## Scope of Change

This breaking change affects **all uses of `Flux.take(n)`**:

```java
// AFFECTED — prefetch semantics changed
Flux.range(1, 100).take(10)
flux.take(limit)

// NOT affected
flux.filter(...)
flux.map(...)
flux.distinct()
```

## Audit Checklist

Search your codebase for `take()` usage and backpressure assumptions:

```bash
# Find all .take() calls
grep -r "\.take(" --include="*.java" src/

# Find metrics/monitoring that track demand
grep -r "doOnRequest\|recordDemand" --include="*.java" src/

# Find explicit request() calls (may need updating)
grep -r "\.request(Long.MAX_VALUE)" --include="*.java" src/
```

## References

- Reactor 2025.0 Release: https://github.com/reactor/reactor-core/releases/tag/v3.7.0
- Reactor Core Release Notes: https://github.com/reactor/reactor-core/releases
- Reactive Streams Specification (backpressure): http://www.reactive-streams.org/
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
