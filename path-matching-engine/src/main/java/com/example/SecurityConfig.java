package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Demonstrates the Spring Security 7.0 removal of AntPathRequestMatcher.
 *
 * On Spring Boot 3.5 (Spring Security 6.5):
 *   org.springframework.security.web.util.matcher.AntPathRequestMatcher exists.
 *   Code that imports and instantiates it compiles cleanly.
 *
 * On Spring Boot 4.0 (Spring Security 7.0):
 *   AntPathRequestMatcher is removed. The import fails:
 *     "package org.springframework.security.web.util.matcher does not exist"
 *     "cannot find symbol: class AntPathRequestMatcher"
 *   Migrate to PathPatternRequestMatcher in
 *   org.springframework.security.web.servlet.util.matcher.
 *
 * This is a Tier 1 (Won't Compile) failure: the build fails before the
 * test ever runs.
 *
 * Reference: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * The pre-7.0 way to build a request matcher. Imports break on Boot 4.0.
     */
    public static RequestMatcher legacyMatcher(String pattern) {
        return new AntPathRequestMatcher(pattern);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(legacyMatcher("/public/**")).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
