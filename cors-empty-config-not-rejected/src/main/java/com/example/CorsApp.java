package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class CorsApp {
    public static void main(String[] args) {
        SpringApplication.run(CorsApp.class, args);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Register an EMPTY configuration. 
        // In Boot 3.5, this might be rejected or result in a default-deny.
        // In Boot 4.0, there are changes in how pre-flight requests are handled.
        source.registerCorsConfiguration("/**", new CorsConfiguration());
        
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @RestController
    public static class HelloController {
        @GetMapping("/hello")
        public String hello() {
            return "Hello";
        }
    }
}
