package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests Spring Security 7.0 removal of AntPathRequestMatcher.
 *
 * On Spring Boot 3.5 (Spring Security 6.5): import resolves, test runs and passes.
 * On Spring Boot 4.0 (Spring Security 7.0): import fails to compile.
 *   "package org.springframework.security.web.util.matcher does not exist"
 *   "cannot find symbol: class AntPathRequestMatcher"
 *
 * Reference: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
 */
class PathMatchingEngineTest {

    @Test
    void antPathRequestMatcherShouldExist() {
        RequestMatcher matcher = new AntPathRequestMatcher("/public/**");
        assertNotNull(matcher,
                "AntPathRequestMatcher should be available on Spring Security 6.5 (Boot 3.5). " +
                "On Security 7.0 (Boot 4.0), this class is removed in favour of " +
                "PathPatternRequestMatcher.");
    }

    @Test
    void antPathRequestMatcherIsAvailableOnClasspath() {
        // Belt-and-braces: even if the import resolves at compile time,
        // make sure the class can be loaded at runtime.
        assertTrue(SecurityConfig.legacyMatcher("/public/**") instanceof AntPathRequestMatcher,
                "legacyMatcher should return an AntPathRequestMatcher on Boot 3.5.");
    }
}
