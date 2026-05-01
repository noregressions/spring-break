package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import static org.junit.jupiter.api.Assertions.*;

class HttpHeadersTest {
    @Test
    void testHttpHeadersAsMultiValueMap() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Test", "Value");
        assertTrue(HttpHeadersUsage.checkHeader(headers, "Test"));
        MultiValueMap<String, String> map = HttpHeadersUsage.asMultiValueMap(headers);
        assertEquals(1, map.size());
    }
}
