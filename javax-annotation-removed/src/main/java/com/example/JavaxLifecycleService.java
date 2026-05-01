package com.example;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Demonstrates javax.annotation lifecycle annotations removed in Spring Boot 4.0.
 *
 * On Boot 3.5: @PostConstruct and @PreDestroy from javax.annotation work correctly.
 * On Boot 4.0: javax.annotation-api is no longer on the classpath.
 *   Compilation fails: package javax.annotation does not exist
 *
 * Fix: Replace javax.annotation imports with jakarta.annotation:
 *   import jakarta.annotation.PostConstruct;
 *   import jakarta.annotation.PreDestroy;
 * Add dependency: jakarta.annotation:jakarta.annotation-api (managed by Boot BOM)
 */
@Component
public class JavaxLifecycleService {

    private boolean postConstructCalled = false;
    private boolean preDestroyCalled = false;

    @PostConstruct
    public void init() {
        // Called by Spring after dependency injection — removed in Boot 4.0
        this.postConstructCalled = true;
    }

    @PreDestroy
    public void cleanup() {
        // Called by Spring before bean destruction — removed in Boot 4.0
        this.preDestroyCalled = true;
    }

    public boolean isPostConstructCalled() {
        return postConstructCalled;
    }

    public boolean isPreDestroyCalled() {
        return preDestroyCalled;
    }
}
