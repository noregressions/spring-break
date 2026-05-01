package com.example;

import org.springframework.stereotype.Service;

/**
 * Second service for demonstrating @SpyBean independently of @MockBean.
 */
@Service
public class HelperService {

  public String format(String template, String value) {
    return template.replace("{}", value);
  }

  public int doubleIt(int n) {
    return n * 2;
  }
}
