# `@ConditionalOnEnabledTracing` Renamed (Tier 1: Won't Build)

**Summary**: Spring Boot 4.0 renames `@ConditionalOnEnabledTracing` to `@ConditionalOnEnabledTracingExport` (paired with the matching property rename). User code, library code, or starter modules that reference the old annotation directly fail to compile against Boot 4.0.

## What Breaks

The official Spring Boot 4.0 Migration Guide states:

> `ConditionalOnEnabledTracing` has been renamed to `ConditionalOnEnabledTracingExport`.

The class `org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing` no longer exists in Boot 4.0. Any user code, library code, or platform module that imports the old annotation directly fails at `javac`. The audience is mostly starter authors and corporate platform teams that wrote their own conditional configuration mirroring Boot's gating idiom — application code rarely references this annotation directly.

## How This Test Works

The module declares a `@Configuration @ConditionalOnEnabledTracing` class — the kind of code a third-party starter or platform team would write to gate a tracing-only extension. The proof is purely at the compile layer: if the user code compiles, the Boot version still has the old annotation; if javac fails, the rename has landed.

- **UserTracingExtension.java** — the user-defined `@Configuration` class importing `@ConditionalOnEnabledTracing`
- **TracingApp.java** — minimal `@SpringBootApplication`
- **ConditionalOnEnabledTracingRenamedTest.java** — a context-loads test that verifies the application starts. We deliberately do NOT assert on the conditional bean firing at runtime: Spring Boot's test infrastructure overrides `management.tracing.enabled=false` by default to suppress observability noise during tests, and the opt-out (`@AutoConfigureObservability`) is itself Boot-version-fragile. Compile success is the load-bearing assertion; the runtime test exists only to prove the module is wired into the suite.

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Builds and passes. `UserTracingExtension` compiles, the conditional fires, the marker bean is in the context.

## On Spring Boot 4.0

```bash
mvn compile -Dspring-boot.version=4.0.6
```

**Result**: ✗ Build fails at compile.

**Error**:
```
[ERROR] UserTracingExtension.java:[3,62]
  package org.springframework.boot.actuate.autoconfigure.tracing does not exist
[ERROR] UserTracingExtension.java:[25,2]
  cannot find symbol
  symbol: class ConditionalOnEnabledTracing
```

`javac` cannot resolve the import; the build stops before tests are compiled or run.

## Fix / Migration Path

Rename the import and the annotation:

```diff
- import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing;
+ import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracingExport;

  @Configuration
- @ConditionalOnEnabledTracing
+ @ConditionalOnEnabledTracingExport
  public class UserTracingExtension {
      ...
  }
```

If the user-defined configuration also reads the legacy property `management.tracing.enabled` directly, rename that to `management.tracing.export.enabled` — see the sibling test module **tracing-export-property-renamed** for the silent regression that the property rename causes when left unfixed.

## Note on Boot test infrastructure

Spring Boot's `@SpringBootTest` injects a synthetic `test` PropertySource that defaults `management.tracing.enabled=false` to suppress observability noise during test runs. The opt-out is `@AutoConfigureObservability` — without it, this module's `@ConditionalOnEnabledTracing` would be evaluated against the test default of `false` and the marker bean would never be wired, even on Boot 3.5. Application teams whose tests depend on tracing being active should be aware of this default; it bites independently of the rename.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) — search for `ConditionalOnEnabledTracing`
- Sibling module: `tracing-export-property-renamed` (silent property rename, Tier 3)
