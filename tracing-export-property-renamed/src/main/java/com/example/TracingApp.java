package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal application — the auto-configuration of Brave is what we're testing.
 * No application code is needed to demonstrate the regression.
 */
@SpringBootApplication
public class TracingApp {
    public static void main(String[] args) {
        SpringApplication.run(TracingApp.class, args);
    }
}
