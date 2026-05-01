package com.example;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class HttpHeadersUsage {
    public static boolean checkHeader(HttpHeaders headers, String headerName) {
        return headers.containsKey(headerName);
    }
    public static MultiValueMap<String, String> asMultiValueMap(HttpHeaders headers) {
        return headers;
    }
    @SuppressWarnings("unchecked")
    public static int getMapSize(HttpHeaders headers) {
        return ((Map<String, java.util.List<String>>) headers).size();
    }
}
