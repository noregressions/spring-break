package com.example;

import org.springframework.stereotype.Service;

/**
 * Simple service implementation for testing.
 */
@Service
public class MyServiceImpl implements MyService {

  @Override
  public String greet(String name) {
    return "Hello, " + name + "!";
  }

  @Override
  public int calculate(int a, int b) {
    return a + b;
  }
}
