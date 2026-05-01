package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.14 — PropertyMapper.alwaysApplyingWhenNonNull() removed. */
class PropertyMapperTest {

    @Test
    void methodShouldExistOnBoot35() {
        assertDoesNotThrow(PropertyMapperUsage::useRemovedMethod);
    }
}
