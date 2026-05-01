package com.example;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 2.14 — SpringExtension method scope */
@ExtendWith(SpringExtension.class)
public class NestedSpringTest {

    @Test
    void outerTest() {
    }

    @Nested
    class InnerTests {
        @Test
        void innerTest() {
            // In Spring Framework 7.0 (Boot 4.0), SpringExtension uses 
            // test-method scoped ExtensionContext.
            // This test verifies that @SpringExtensionConfig is NOT required for basic nested tests,
            // but the change in scope is the relevant migration point.
            // A more complex test would check TestExecutionListener behavior.
        }
    }

    @Test
    void springExtensionConfigShouldNotExistOnBoot35() {
        // This is a way to detect Boot 4.0 (Spring 7.0)
        // SpringExtensionConfig was added in Spring 7.0.
        boolean exists = false;
        try {
            Class.forName("org.springframework.test.context.junit.jupiter.SpringExtensionConfig");
            exists = true;
        } catch (ClassNotFoundException e) {
            exists = false;
        }
        
        // Assert false on 3.5 to pass.
        // On 4.0 this will be true, and we can fail the test to show the difference.
        if (exists) {
            throw new AssertionError("SpringExtensionConfig exists! This indicates Spring Framework 7.0+ (Boot 4.0). " +
                "The default scope for SpringExtension has changed to TEST_METHOD.");
        }
    }
}
