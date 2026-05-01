package com.example;

import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.DefaultBootstrapContext;

public class BootstrapRegistryUsage {
    public static Object createRegistry() {
        return new DefaultBootstrapContext();
    }
}
