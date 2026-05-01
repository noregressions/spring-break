package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

/**
 * Spring Boot application with Spring Batch enabled.
 *
 * @EnableBatchProcessing auto-configures the batch infrastructure.
 */
@SpringBootApplication
@EnableBatchProcessing
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
