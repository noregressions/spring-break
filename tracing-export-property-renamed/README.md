# `management.tracing.enabled` Silently Renamed (Tier 3: Different Results)

**Summary**: Spring Boot 4.0 renames `management.tracing.enabled` to `management.tracing.export.enabled`. The legacy property is silently ignored — no warning, no error. A Boot 3.x application that explicitly *disabled* tracing in non-production environments will silently start exporting trace context again after the upgrade.

## What Breaks

The official Spring Boot 4.0 Migration Guide states:

> The property `management.tracing.enabled` has been renamed to `management.tracing.export.enabled`.

Boot 4.0 does not log a deprecation warning, does not honour the legacy property, and does not migrate it for you. The legacy key is treated as an unknown property; the new key falls back to its default of `true` when tracing dependencies are present. The result: an environment configured to disable tracing on Boot 3.x has tracing silently re-enabled on Boot 4.0.

## How This Test Works

The module sets `management.tracing.enabled=false` in `application.properties` (the legacy form) and asserts that Brave's `propagationFactory` bean — which is gated by `@ConditionalOnEnabledTracing` — is NOT wired. A noop `Propagation.Factory` should be in the context as the fallback.

- **TracingApp.java** — minimal `@SpringBootApplication`
- **application.properties** — sets the legacy `management.tracing.enabled=false`
- **TracingExportPropertyRenameTest.java** — asserts `propagationFactory` is absent and `noopPropagationFactory` is present
- **pom.xml** — depends on `spring-boot-starter-actuator` + `micrometer-tracing-bridge-brave`. A Maven profile activated when `-Dspring-boot.version=4.0.6` adds `spring-boot-micrometer-tracing-brave` (without it, Boot 4.0's tracing auto-config doesn't load at all because of the modular-actuator restructure)

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Test passes. `@ConditionalOnEnabledTracing` reads `management.tracing.enabled=false` and skips Brave's real propagation factory; the noop fallback is wired.

## On Spring Boot 4.0.6

```bash
mvn test -Dspring-boot.version=4.0.6
```

**Result**: ✗ Test fails.

**Output**:
```
TracingExportPropertyRenameTest.legacyPropertyDisablesPropagationFactory:66
  When management.tracing.enabled=false the real propagationFactory bean
  (gated by @ConditionalOnEnabledTracing) should NOT be wired.
  expected: <false> but was: <true>
```

The legacy property is silently ignored on 4.0. `@ConditionalOnEnabledTracingExport` reads the new property name, finds it unset, applies its default (`true`), and Brave's real propagation factory is wired despite the user's intent to disable tracing.

## Fix / Migration Path

Rename the property in every `application.properties`, `application.yml`, environment-variable mapping, and external configuration source:

```diff
- management.tracing.enabled=false
+ management.tracing.export.enabled=false
```

The corresponding `@ConditionalOnEnabledTracing` annotation has also been renamed to `@ConditionalOnEnabledTracingExport` — see the sibling test module **conditional-on-enabled-tracing-renamed** for the compile-break that user code referencing the old annotation hits.

## Scope of Proof

This test demonstrates the silent regression in property handling. It does **not** quantify the cost of accidentally re-enabled tracing (extra latency, exporter network traffic, observability backend cost) — that's environment-specific. The test is sufficient to prove the property is silently lost; the operational impact is left for the migrating team to assess.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) — search for `management.tracing.enabled`
- [Spring Framework 7 — observation conventions](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-7.x)
- Sibling module: `conditional-on-enabled-tracing-renamed` (annotation-rename compile break)
