package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.entities")
public class EntityScanApp {
    public static void main(String[] args) {
        SpringApplication.run(EntityScanApp.class, args);
    }
}
