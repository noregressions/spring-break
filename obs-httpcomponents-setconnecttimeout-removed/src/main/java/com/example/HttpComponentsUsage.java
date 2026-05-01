package com.example;

import org.apache.hc.client5.http.config.RequestConfig;
import java.util.concurrent.TimeUnit;

public class HttpComponentsUsage {
    public static void configureTimeout() {
        // RequestConfig.Builder.setSocketTimeout(int) was removed in HttpClient 5.0.
        // Users migrating from HttpClient 4.x (Boot 2.x) to HttpClient 5.x (Boot 3.x/4.x) will hit this.
        RequestConfig.custom()
            .setSocketTimeout(5000)
            .build();
    }
}
