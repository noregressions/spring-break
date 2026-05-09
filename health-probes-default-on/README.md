# Health Probes Default On (Tier 3: Different Results)

**Summary**: Spring Boot 4.0 silently enables liveness and readiness probes for all applications. On Boot 3.5 the probes were only auto-configured on Kubernetes (or when explicitly enabled). On Boot 4.0 they're always on. The `/actuator/health` JSON gains a `groups` field; `/actuator/health/liveness` and `/actuator/health/readiness` flip from `404` to `200`. No warning, no log message — downstream consumers parsing the body or relying on the missing endpoints can break silently.

## What Breaks

The behaviour differs at three observable layers:

| Layer | Boot 3.5.14 default | Boot 4.0.6 default |
|---|---|---|
| `livenessStateHealthIndicator` / `readinessStateHealthIndicator` beans | absent | auto-configured |
| `GET /actuator/health` body | `{"status":"UP"}` | `{"groups":["liveness","readiness"],"status":"UP"}` |
| `GET /actuator/health/liveness` | `404` | `200 {"status":"UP"}` |
| `GET /actuator/health/readiness` | `404` | `200 {"status":"UP"}` |

The change is silent — Boot logs nothing about the new defaults during startup or migration. The most likely failure modes:

- **JSON parsers** that did exact-shape matching on `/actuator/health` see a new top-level key.
- **Monitoring rules** that alerted on `404` from probe URLs go silent (no data ≠ no problem).
- **Security rules** that scoped exposure to `/actuator/health` may have inadvertently exposed `/actuator/health/liveness` and `/actuator/health/readiness` — they're sub-paths of the same endpoint.

## How This Test Works

`HealthProbesTest` runs the application on a random port and asserts each layer:
- **Bean-level**: `livenessStateHealthIndicator` and `readinessStateHealthIndicator` not in the context
- **HTTP status**: `/actuator/health/liveness` and `/actuator/health/readiness` return `404`
- **HTTP body**: `/actuator/health` body does not contain a `"groups"` key

All three pass on Boot 3.5 default; all three fail on Boot 4.0 default. Each assertion's failure message points at the specific symptom and the prescribed fix.

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ 3 tests pass.

## On Spring Boot 4.0.6

```bash
mvn test -Dspring-boot.version=4.0.6
```

**Result**: ✗ 3 tests fail with informative messages:

```
expected: <false> but was: <true>   (livenessStateHealthIndicator bean is present)
expected: <404> but was: <200>      (probe endpoints reachable)
Body was: {"groups":["liveness","readiness"],"status":"UP"}   (new groups key)
```

## Fix / Migration Path

To restore Boot 3.5 default behaviour exactly:

```properties
management.endpoint.health.probes.enabled=false
```

Empirically verified: with this property set on Boot 4.0, `/actuator/health` returns `{"status":"UP"}` (no `groups` key), and `/actuator/health/liveness` / `/actuator/health/readiness` return `404`. Identical to Boot 3.5 default.

If your deployment _does_ use the probes (Kubernetes, load balancers, service mesh health checks), leave them on but make sure your security rules and monitoring updates account for the new endpoints.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Boot — Liveness and Readiness Probes](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.kubernetes-probes)
- Master list entry: 2.2 (was M16 in the audit)
