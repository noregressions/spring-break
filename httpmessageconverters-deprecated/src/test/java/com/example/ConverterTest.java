package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Master list: 2.3 — HttpMessageConverters deprecated */
@SpringBootTest
public class ConverterTest {

    @Autowired(required = false)
    private HttpMessageConverters converters;

    @Test
    void httpMessageConvertersBeanShouldExistOnBoot35() {
        // In Boot 3.5, declaring an HttpMessageConverters bean works and is auto-configured.
        // In Boot 4.0, HttpMessageConvertersAutoConfiguration is removed, so the bean 
        // might not be auto-processed or the class itself might be deprecated/removed.
        assertNotNull(converters, 
            "HttpMessageConverters bean should be injected on Boot 3.5. " +
            "In Boot 4.0, HttpMessageConvertersAutoConfiguration is removed.");
    }
}
