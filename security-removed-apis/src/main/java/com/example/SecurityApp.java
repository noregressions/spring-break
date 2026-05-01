package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SecurityApp {

    @GetMapping("/public/hello")
    public String publicEndpoint() {
        return "Public endpoint — no auth required";
    }

    @GetMapping("/admin/dashboard")
    public String adminEndpoint() {
        return "Admin endpoint — requires ADMIN role";
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "Protected endpoint — requires authentication";
    }

    public static void main(String[] args) {
        SpringApplication.run(SecurityApp.class, args);
    }
}
