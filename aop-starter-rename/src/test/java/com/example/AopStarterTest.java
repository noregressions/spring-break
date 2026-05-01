package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.10 — AOP starter renamed to AspectJ starter. */
@SpringBootTest
class AopStarterTest {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
            // Just verifying the starter is present and functional on 3.5
        });
    }

    @Test
    void aspectjClassPresent() {
        assertDoesNotThrow(() -> {
            Class.forName("org.aspectj.lang.JoinPoint");
        });
    }
}
