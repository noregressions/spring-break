package com.example;

import javax.inject.Named;

@Named
public class GreetingService {

    public String greet(String name) {
        return "Hello, " + name;
    }
}
