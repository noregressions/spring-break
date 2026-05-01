package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.34 — HttpComponents setConnectTimeout removed. */
class HttpComponentsTimeoutTest {

    @Test
    void setSocketTimeoutShouldNotExitOnHttpClient5() {
        // Direct use — fails to compile on HttpClient 5.x because setSocketTimeout(int) was removed.
        assertDoesNotThrow(() -> HttpComponentsUsage.configureTimeout());
    }
}
