package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.60 — ApacheDS LDAP support removed. */
class ApacheDsTest {

    @Test
    void containerShouldExistOnBoot35() {
        assertTrue(ApacheDsUsage.isContainerPresent(),
            "ApacheDSContainer should be detectable on Boot 3.5");
    }

    @Test
    void classIsLoadableViaReflection() {
        assertTrue(ApacheDsUsage.isContainerPresent(),
            "ApacheDSContainer should be detectable on Boot 3.5");
    }
}
