package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/* Master list: 1.46 — Elasticsearch RestClient renamed to Rest5Client. */
@SpringBootTest
class ElasticsearchRestClientTest {

    @Autowired(required = false)
    private ElasticsearchUsage usage;

    @Test
    void restClientShouldBeInjectedOnBoot35() {
        assertNotNull(usage, "ElasticsearchUsage component should be present");
        assertNotNull(usage.getRestClient(), 
            "RestClient should be auto-configured and injected on Boot 3.5");
    }
}
