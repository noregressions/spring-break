package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Verifies that the OpenSaml4AuthenticationProvider class exists on the classpath.
 *
 * Spring Boot 3.5: class exists — test passes.
 * Spring Boot 4.0: class removed (only OpenSAML 5 supported) — ClassNotFoundException.
 */
class OpenSaml4RemovedTest {

    @Test
    void openSaml4AuthenticationProviderShouldExistOnClasspath() {
        // Use initialize=false so the JVM loads the class bytecode without
        // resolving OpenSAML transitive dependencies at static-init time.
        assertDoesNotThrow(
            () -> Class.forName(
                "org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider",
                false,
                Thread.currentThread().getContextClassLoader()
            ),
            "OpenSaml4AuthenticationProvider should be on the classpath. "
                + "In Boot 4.0 this class is removed — only OpenSAML 5 is supported."
        );
    }
}
