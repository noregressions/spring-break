package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (a) — Won't Compile on Boot 4.0
 *
 * On Boot 3.5: javax.annotation-api is on the classpath, @PostConstruct
 *   and @PreDestroy are recognised and called by Spring. Tests pass.
 *
 * On Boot 4.0: javax.annotation-api is no longer available.
 *   Compilation fails: package javax.annotation does not exist
 *
 * Fix: Migrate to jakarta.annotation.PostConstruct / PreDestroy.
 *   The jakarta.annotation-api artifact is managed by the Boot BOM.
 */
@SpringBootTest
class JavaxAnnotationTest {

    @Autowired
    private JavaxLifecycleService service;

    @Test
    void postConstructShouldBeCalled() {
        // On Boot 3.5: passes — @PostConstruct fires during bean initialisation
        // On Boot 4.0: compile error — package javax.annotation does not exist
        assertTrue(service.isPostConstructCalled(),
                "@javax.annotation.PostConstruct was not called by Spring");
    }
}
