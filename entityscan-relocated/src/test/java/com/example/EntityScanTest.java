package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.47 — @EntityScan package moved to org.springframework.boot.persistence.autoconfigure */
@SpringBootTest
class EntityScanTest {

    @Test
    void contextLoads() {
        // If this runs, @EntityScan was correctly processed (on 3.5)
    }

    @Test
    void entityScanIsLoadableViaLegacyReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.boot.autoconfigure.domain.EntityScan"),
            "@EntityScan should be in org.springframework.boot.autoconfigure.domain on Boot 3.5"
        );
    }
}
