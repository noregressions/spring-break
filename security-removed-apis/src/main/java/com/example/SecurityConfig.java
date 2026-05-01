package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration using deprecated APIs from Security 6.x.
 *
 * On Boot 3.5 (Spring Security 6.x):
 * - authorizeRequests() works (deprecated, but available)
 * - .and() for chaining works (deprecated, but available)
 * - antMatchers() works (deprecated, but available)
 * - Compiles and runs successfully (with deprecation warnings)
 *
 * On Boot 4.0 (Spring Security 7.0):
 * - authorizeRequests() is REMOVED (must use authorizeHttpRequests)
 * - .and() is REMOVED (must use lambda DSL)
 * - antMatchers() is REMOVED (must use requestMatchers)
 * - Compilation fails: cannot find symbol for all three methods
 *
 * This is a Tier 1 failure: build breaks immediately.
 *
 * Migration path:
 * 1. Replace .authorizeRequests() → .authorizeHttpRequests(authz -> ...)
 * 2. Replace .and() → lambda closures (no chaining needed)
 * 3. Replace .antMatchers() → .requestMatchers()
 * 4. Update formLogin()/logout() to lambda syntax
 *
 * References:
 * - Spring Security 7.0 Migration: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
 * - Spring Boot 4.0 Migration: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
@Configuration
@SuppressWarnings("deprecation")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // authorizeRequests() is deprecated in Security 6.x, removed in 7.0
            .authorizeRequests()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            // .and() is deprecated in Security 6.2, removed in 7.0
            .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
            .and()
                .logout()
                    .permitAll();

        return http.build();
    }
}
