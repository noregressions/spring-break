package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Master list: 2.17 — CORS empty config not rejected */
@SpringBootTest
@AutoConfigureMockMvc
public class CorsPreflightTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void preflightWithEmptyConfigShouldBeRejectedOnBoot35() throws Exception {
        // In Spring 3.5, an empty CorsConfiguration (no allowed origins, etc) 
        // results in a 403 Forbidden for pre-flight requests.
        // In Spring 4.0, there is a behavior change where it might not be rejected
        // in the same way or headers might differ.
        
        mockMvc.perform(options("/hello")
                .header("Origin", "https://example.com")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
