package com.example;

import javax.inject.Inject;

public class GreetingController {

    private final GreetingService greetingService;

    @Inject
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public String greet(String name) {
        return greetingService.greet(name);
    }
}
