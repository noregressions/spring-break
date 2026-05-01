package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class RetryApp {
    public static void main(String[] args) {
        SpringApplication.run(RetryApp.class, args);
    }
}
