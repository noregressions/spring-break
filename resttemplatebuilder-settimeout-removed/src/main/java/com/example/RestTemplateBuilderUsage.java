package com.example;

import org.springframework.boot.web.client.RestTemplateBuilder;
import java.time.Duration;

public class RestTemplateBuilderUsage {
    public static void useRemovedMethods() {
        new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(1))
            .setReadTimeout(Duration.ofSeconds(1));
    }
}
