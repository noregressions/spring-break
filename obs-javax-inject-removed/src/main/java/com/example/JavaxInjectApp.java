package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JavaxInjectApp {

    public static void main(String[] args) {
        SpringApplication.run(JavaxInjectApp.class, args);
    }

    @Bean
    public GreetingController greetingController(GreetingService greetingService) {
        return new GreetingController(greetingService);
    }
}
