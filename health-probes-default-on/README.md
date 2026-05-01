# Health Probes Default On (Tier 2: Won't Run)

**Summary**: In Spring Boot 4.0, liveness and readiness probes are enabled by default. Previously, they were only auto-configured when running on a platform like Kubernetes or when explicitly enabled.

## What breaks

In Spring Boot 3.5, liveness and readiness health groups are typically absent unless:
1. The application is running on Kubernetes (detected via `KUBERNETES_SERVICE_HOST`).
2. `management.endpoint.health.probes.enabled` is set to `true`.

In Spring Boot 4.0, these probes are enabled by default regardless of the environment. This means new health groups (`liveness` and `readiness`) will appear at `/actuator/health/liveness` and `/actuator/health/readiness`. This might conflict with existing custom health groups or expose state that was previously hidden.

```properties
# No longer needed in Spring Boot 4.0
management.endpoint.health.probes.enabled=true
```

## How this test works

The module `health-probes-default-on` contains:
- `ProbesApp.java`: A standard Spring Boot application with Actuator.
- `HealthProbesTest.java`: A test that checks for the absence of the `liveness` and `readiness` health groups.

On Boot 3.5: The groups are absent, and the test passes.
On Boot 4.0: The groups are present by default, and the test fails (asserting that they should be absent to demonstrate the change).

## Fix / Migration Path

If you want to preserve the previous behavior and hide these probes, you must explicitly disable them.

```properties
# Spring Boot 4.0 (Fixed to restore old behavior)
management.endpoint.health.probes.enabled=false
```

## References

- [Liveness and readiness probes are enabled by default in Spring Boot 4](https://dimitri.codes/actuator-health-probes/)
- Master list entry: 2.2
