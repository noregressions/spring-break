package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Category (c) — Runtime Errors on 4.0
 *
 * Standard OAuth2 client configuration without explicit PKCE parameters.
 * Uses lambda DSL (compatible with both Security 6.x and 7.x).
 *
 * BREAKING CHANGE in Spring Boot 4.0 (Spring Security 7.x):
 *
 * Spring Security 6.x:
 *   PKCE is only enforced for public clients (no client secret).
 *   Confidential clients (with client secret) use direct authorization code exchange.
 *
 * Spring Security 7.x:
 *   PKCE is enabled by default for ALL clients, including confidential clients.
 *   The framework silently adds code_challenge and code_challenge_method parameters
 *   to the authorization request. Legacy OAuth providers that don't support PKCE
 *   will reject the request with "unsupported_parameter" or similar.
 *
 * The code compiles on both versions. The difference is runtime behaviour: the
 * authorization request on 4.0 includes PKCE parameters that weren't there before.
 *
 * Fix: Upgrade to a modern OAuth provider that supports PKCE, or explicitly
 *   disable PKCE via configuration.
 *
 * References:
 * - PKCE RFC 7636: https://datatracker.ietf.org/doc/html/rfc7636
 * - OAuth 2.1 (RFC 9700): https://datatracker.ietf.org/doc/html/rfc9700
 * - Spring Security 7.0 Migration: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
@Configuration
@EnableWebSecurity
public class OAuth2ClientConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login**").permitAll()
                .anyRequest().authenticated()
            )
            // Lambda DSL — compiles on both Security 6.x and 7.x.
            // On 6.x: no PKCE added for confidential clients.
            // On 7.x: PKCE parameters added automatically — providers
            //   that don't support PKCE will reject the request.
            .oauth2Login(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutSuccessUrl("/")
            );

        return http.build();
    }
}
