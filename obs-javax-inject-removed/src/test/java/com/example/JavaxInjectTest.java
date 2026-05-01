package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * On Spring Boot 3.5: javax.inject works alongside Spring, test passes.
 * On Spring Boot 4.0: javax.inject imports fail to compile (package doesn't exist).
 */
@SpringBootTest(classes = JavaxInjectApp.class)
class JavaxInjectTest {

    @Autowired
    private GreetingController greetingController;

    @Test
    void shouldInjectAndGreet() {
        assertEquals("Hello, World", greetingController.greet("World"));
    }
}
