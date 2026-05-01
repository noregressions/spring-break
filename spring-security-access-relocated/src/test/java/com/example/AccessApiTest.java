package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.62 — Access API moved to legacy spring-security-access module. */
class AccessApiTest {

    @Test
    void managerShouldExistOnBoot35() {
        Object manager = AccessApiUsage.createManager();
        assertNotNull(manager);
    }

    @Test
    void classIsLoadableViaReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.security.access.AccessDecisionManager"),
            "AccessDecisionManager should be on classpath on Boot 3.5. Moved to separate module in 4.0."
        );
    }
}
