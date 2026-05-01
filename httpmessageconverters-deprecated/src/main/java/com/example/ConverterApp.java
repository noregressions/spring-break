package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

@SpringBootApplication
public class ConverterApp {
    public static void main(String[] args) {
        SpringApplication.run(ConverterApp.class, args);
    }

    @Bean
    public HttpMessageConverters customConverters() {
        // This class HttpMessageConverters is deprecated in Spring Boot 4.0
        // in favor of ClientHttpMessageConvertersCustomizer and ServerHttpMessageConvertersCustomizer.
        return new HttpMessageConverters(new StringHttpMessageConverter());
    }
}
