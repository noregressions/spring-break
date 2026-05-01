package com.example;

import org.springframework.security.messaging.util.matcher.SimpDestinationMessageMatcher;

public class SimpDestUsage {
    public static Object createMatcher() {
        return new SimpDestinationMessageMatcher("/topic/**");
    }
}
