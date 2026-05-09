package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tier 1 — compile break.
 *
 * Spring Boot 3.5.14:
 * - org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest exists in
 *   spring-boot-test-autoconfigure. The annotation is one of the most widely-used
 *   slice annotations in real codebases.
 * - This test class compiles and runs; MockMvc is wired and the controller test passes.
 *
 * Spring Boot 4.0.6:
 * - The annotation moved to spring-boot-webmvc-test, with the package renamed to
 *   org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest.
 * - The Boot-3 import does not resolve. javac fails before tests are compiled:
 *     "package org.springframework.boot.test.autoconfigure.web.servlet does not exist"
 *
 * The same pattern applies across the test-side annotations:
 *   @AutoConfigureMockMvc           → spring-boot-webmvc-test (org.springframework.boot.webmvc.test.autoconfigure)
 *   @AutoConfigureWebClient         → spring-boot-webclient-test (org.springframework.boot.webclient.test.autoconfigure)
 *   @AutoConfigureMockRestServiceServer → spring-boot-restclient-test (org.springframework.boot.restclient.test.autoconfigure)
 *   @AutoConfigureWebFlux           → spring-boot-webflux-test (org.springframework.boot.webflux.test.autoconfigure)
 *   @WebFluxTest                    → spring-boot-webflux-test (org.springframework.boot.webflux.test.autoconfigure)
 *   @DataJpaTest                    → spring-boot-data-jpa-test (org.springframework.boot.data.jpa.test.autoconfigure)
 *   @DataMongoTest                  → spring-boot-data-mongodb-test (org.springframework.boot.data.mongodb.test.autoconfigure)
 *   @JdbcTest                       → spring-boot-jdbc-test (org.springframework.boot.jdbc.test.autoconfigure)
 *   @RestClientTest                 → spring-boot-restclient-test (org.springframework.boot.restclient.test.autoconfigure)
 *   @GraphQlTest                    → spring-boot-graphql-test (org.springframework.boot.graphql.test.autoconfigure)
 *
 * @AutoConfigureObservability is the exception — it was REMOVED entirely
 * (along with the suppression mechanism it opted out of). See the sibling
 * test module auto-configure-observability-removed.
 *
 * Migration on Boot 4: add the per-concern test module (or its starter) to
 * the build, then update the import package. The most common pair:
 *   <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-webmvc-test</artifactId>
 *     <scope>test</scope>
 *   </dependency>
 * and
 *   import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
 */
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloEndpointReturnsExpectedBody() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, world"));
    }
}
