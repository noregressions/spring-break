package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests Spring Security configuration using Boot 3.5 deprecated APIs.
 *
 * On Boot 3.5 (Security 6.x):
 * - SecurityConfig compiles with deprecation warnings
 * - These tests pass; security rules work as expected
 *
 * On Boot 4.0 (Security 7.0):
 * - SecurityConfig fails to compile (authorizeRequests(), .and() removed)
 * - These tests never run; build fails at compile time
 *
 * Tier 1 failure: Boot 4.0 removes the APIs entirely, breaking the build.
 *
 * To fix: Rewrite SecurityConfig to use lambda DSL:
 * - Replace .authorizeRequests() with .authorizeHttpRequests(authz -> ...)
 * - Remove .and() chaining; use lambda closures instead
 * - Update .antMatchers() to .requestMatchers()
 *
 * See: https://docs.spring.io/spring-security/reference/6.5/migration-7/configuration.html
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpointIsAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/public/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/protected"))
                .andExpect(status().is3xxRedirection()); // redirects to /login
    }

    @Test
    void adminEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }
}
