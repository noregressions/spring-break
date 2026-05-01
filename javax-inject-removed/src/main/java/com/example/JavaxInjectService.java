package com.example;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.ApplicationContext;

/**
 * Demonstrates javax.inject annotations removed in Spring Boot 4.0.
 *
 * On Boot 3.5: @Inject and @Named from javax.inject work as Spring-recognised
 *   equivalents of @Autowired and @Component. Tests pass.
 *
 * On Boot 4.0: javax.inject:javax.inject is no longer on the classpath.
 *   Compilation fails: package javax.inject does not exist
 *
 * Fix: Replace javax.inject with jakarta.inject:
 *   import jakarta.inject.Inject;
 *   import jakarta.inject.Named;
 * Or switch to Spring-native annotations (@Autowired / @Component).
 * Add dependency: jakarta.inject:jakarta.inject-api (managed by Boot BOM).
 */
@Named("javaxInjectService")
public class JavaxInjectService {

    @Inject
    private ApplicationContext context;

    public ApplicationContext getContext() {
        return context;
    }
}
