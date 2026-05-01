package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (a) — Won't Compile on Boot 4.0
 *
 * On Boot 3.5: javax.inject is on the classpath. @Inject is recognised as
 *   equivalent to @Autowired and @Named as equivalent to @Component.
 *   Tests pass.
 *
 * On Boot 4.0: javax.inject:javax.inject is no longer available.
 *   Compilation fails: package javax.inject does not exist
 *
 * Fix: Migrate to jakarta.inject.Inject / Named, or switch to
 *   Spring-native @Autowired / @Component entirely.
 */
@SpringBootTest
class JavaxInjectTest {

    @Autowired
    private JavaxInjectService service;

    @Test
    void injectShouldWireApplicationContext() {
        // On Boot 3.5: passes — @Inject wires ApplicationContext correctly
        // On Boot 4.0: compile error — package javax.inject does not exist
        assertNotNull(service.getContext(),
                "@javax.inject.Inject failed: ApplicationContext was not injected");
    }
}
