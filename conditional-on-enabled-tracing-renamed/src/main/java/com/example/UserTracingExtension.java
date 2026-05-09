package com.example;

import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A user-defined extension that runs only when tracing is enabled — for example,
 * a custom span exporter, a tracing-aware repository hook, or a starter library
 * authored for Boot 3.x that mirrors Boot's own gating idiom.
 *
 * Spring Boot 3.5: org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing
 *                  resolves. The class compiles and the conditional bean is created when
 *                  management.tracing.enabled=true (the default with tracing deps on the classpath).
 *
 * Spring Boot 4.0: this annotation has been renamed to ConditionalOnEnabledTracingExport
 *                  (and the property to management.tracing.export.enabled). The old import
 *                  no longer resolves, javac fails before any tests can run.
 *
 * Library authors and corporate platform teams that wrote @ConditionalOnEnabledTracing-gated
 * configurations against Boot 3 will see this as a compile break the moment they consume
 * Boot 4. Application code is much less likely to use this annotation directly.
 */
@Configuration
@ConditionalOnEnabledTracing
public class UserTracingExtension {

    @Bean
    public String tracingExtensionMarker() {
        return "user-tracing-extension-active";
    }
}
