package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Category (c) — Runtime Error on 4.0
 *
 * Demonstrates that org.springframework.lang.Nullable is no longer recognised
 * by Actuator's parameter binding in Spring Boot 4.0.
 *
 * Spring Boot 3.5 (Spring Framework 6.x):
 *   @org.springframework.lang.Nullable on a @ReadOperation parameter marks it
 *   as optional. Calling the endpoint without the parameter returns 200.
 *
 * Spring Boot 4.0 (Spring Framework 7.0 / JSpecify):
 *   @org.springframework.lang.Nullable is no longer recognised by Actuator's
 *   WebOperationRequestPredicate. The parameter is treated as required.
 *   Calling without it returns 400 MissingServletRequestParameterException.
 *
 * The code compiles on both versions — org.springframework.lang.Nullable still
 * exists on the classpath. The failure is silent until the endpoint is called.
 *
 * Fix: replace org.springframework.lang.Nullable with
 *      org.jspecify.annotations.Nullable on all Actuator endpoint parameters.
 */
@SpringBootTest(properties = {
        "management.endpoints.web.exposure.include=nullable",
        "management.endpoints.enabled-by-default=true"
})
@AutoConfigureMockMvc
class ActuatorNullableTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void optionalParameterShouldBeOptional() throws Exception {
        // On Boot 3.5: @org.springframework.lang.Nullable marks 'name' as optional.
        // Calling without the parameter returns 200 OK.
        //
        // On Boot 4.0: @org.springframework.lang.Nullable is not recognised by
        // Actuator binding. 'name' is treated as required → 400 Bad Request.
        mockMvc.perform(get("/actuator/nullable"))
                .andExpect(status().isOk());
    }

    @Test
    void optionalParameterShouldAcceptValue() throws Exception {
        // Calling with the parameter should work on both versions
        mockMvc.perform(get("/actuator/nullable").param("name", "Alice"))
                .andExpect(status().isOk());
    }
}
