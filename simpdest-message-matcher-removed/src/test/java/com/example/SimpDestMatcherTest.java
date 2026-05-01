package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.59 — SimpDestMessageMatcher removed. */
class SimpDestMatcherTest {

    @Test
    void matcherShouldExistOnBoot35() {
        Object matcher = SimpDestUsage.createMatcher();
        assertNotNull(matcher);
    }

    @Test
    void classIsLoadableViaReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.security.messaging.util.matcher.SimpDestinationMessageMatcher"),
            "SimpDestinationMessageMatcher should be on classpath on Boot 3.5. Removed in 4.0."
        );
    }
}
