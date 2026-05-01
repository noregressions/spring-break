package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class FieldBindingApp {

    public static void main(String[] args) {
        SpringApplication.run(FieldBindingApp.class, args);
    }
}
