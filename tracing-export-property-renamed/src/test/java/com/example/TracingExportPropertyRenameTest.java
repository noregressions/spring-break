package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the silent rename of the tracing-enabled property between Boot 3.5 and 4.0.
 *
 * Spring Boot 3.5.14:
 * - The property `management.tracing.enabled` is the canonical key for
 *   `@ConditionalOnEnabledTracing` in Boot's actuator auto-configuration.
 * - With `management.tracing.enabled=false` set in application.properties:
 *     • The `propagationFactory` bean (defined in
 *       BravePropagationConfigurations.PropagationWithBaggage and gated by
 *       @ConditionalOnEnabledTracing) is NOT created.
 *     • A noop CompositePropagationFactory is wired in its place.
 *     • Trace context is not propagated downstream — tracing is effectively disabled.
 * - assertion below passes.
 *
 * Spring Boot 4.0.6:
 * - The property has been renamed to `management.tracing.export.enabled`.
 *   (Source: official Boot 4.0 Migration Guide.)
 * - The annotation has been renamed to `@ConditionalOnEnabledTracingExport`.
 * - The legacy `management.tracing.enabled` is silently ignored.
 * - The new property defaults to TRUE when tracing dependencies are on the classpath.
 * - With only the legacy `=false` set:
 *     • `@ConditionalOnEnabledTracingExport` returns true (default).
 *     • The real `propagationFactory` bean IS created.
 *     • Trace context propagation runs as if tracing were enabled.
 * - The assertion below fires — the test catches the regression.
 *
 * This is a Tier 3 silent regression: no error, no warning, no log message.
 * A Boot 3.x application that explicitly disabled tracing in non-production
 * environments will silently start propagating and exporting trace context after
 * upgrading to 4.0 unless the property is also renamed.
 *
 * Fix: rename `management.tracing.enabled` → `management.tracing.export.enabled`.
 *      Any user code referencing `@ConditionalOnEnabledTracing` directly must
 *      also be renamed to `@ConditionalOnEnabledTracingExport`.
 */
@SpringBootTest
class TracingExportPropertyRenameTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void legacyPropertyDisablesPropagationFactory() {
        // We set management.tracing.enabled=false in application.properties.
        //
        // Bean wiring on each Boot version:
        //   Boot 3.5.14 (legacy property is honoured):
        //     • propagationFactory     -> NOT in context  (gated, false)
        //     • noopPropagationFactory -> in context      (fallback)
        //   Boot 4.0.6 (legacy property is silently ignored, default-true takes over):
        //     • propagationFactory     -> in context      (gated, true)
        //     • noopPropagationFactory -> NOT in context
        boolean realPropagationFactory = context.containsBean("propagationFactory");
        boolean noopPropagationFactory = context.containsBean("noopPropagationFactory");

        assertFalse(
            realPropagationFactory,
            "When management.tracing.enabled=false the real propagationFactory bean " +
            "(gated by @ConditionalOnEnabledTracing) should NOT be wired. " +
            "If this assertion fires on Boot 4.0, the legacy property has been silently " +
            "renamed to management.tracing.export.enabled — update the property name."
        );
        assertTrue(
            noopPropagationFactory,
            "When management.tracing.enabled=false the noop propagation factory should be " +
            "wired as the fallback Propagation.Factory. Missing on Boot 4.0 because the " +
            "real propagationFactory now wins (legacy property ignored)."
        );
    }
}
