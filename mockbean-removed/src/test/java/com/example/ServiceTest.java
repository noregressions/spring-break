package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * Demonstrates Spring Boot 3.x @MockBean and @SpyBean annotations (removed in 4.0).
 *
 * BREAKING CHANGE: Spring Boot 4.0 removed:
 * - @MockBean from org.springframework.boot.test.mock.mockito
 * - @SpyBean from org.springframework.boot.test.mock.mockito
 *
 * MIGRATION: Replace with Mockito-native bean override mechanism:
 * - @MockitoBean (from org.springframework.test.context.bean.override.mockito)
 * - @MockitoSpyBean (from org.springframework.test.context.bean.override.mockito)
 *
 * Reference: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 *
 * Design: @MockBean and @SpyBean are used on SEPARATE beans to avoid
 * NoUniqueBeanDefinitionException conflicts (one mocks MyService,
 * the other spies on HelperService).
 */
@SpringBootTest
public class ServiceTest {

  @MockBean
  private MyService mockService;

  @SpyBean
  private HelperService spyHelper;

  @Test
  public void testMockBean() {
    // @MockBean (3.5) creates a complete mock that replaces the real bean.
    // On 4.0, use @MockitoBean instead.
    // This test verifies stub behavior: when() defines return values,
    // and verify() confirms the mock was called correctly.
    when(mockService.greet("Alice")).thenReturn("Hi Alice!");
    when(mockService.calculate(2, 3)).thenReturn(10);

    String greeting = mockService.greet("Alice");
    int result = mockService.calculate(2, 3);

    assertEquals("Hi Alice!", greeting);
    assertEquals(10, result);

    verify(mockService).greet("Alice");
    verify(mockService).calculate(2, 3);
  }

  @Test
  public void testSpyBean() {
    // @SpyBean (3.5) wraps the real bean, allowing both real execution and verification.
    // On 4.0, use @MockitoSpyBean instead.
    // This test verifies spy behavior: real methods execute, and verify() confirms calls.
    String formatted = spyHelper.format("Hello, {}!", "Bob");
    int doubled = spyHelper.doubleIt(5);

    assertEquals("Hello, Bob!", formatted);
    assertEquals(10, doubled);

    // Verify the real methods were called via spy
    verify(spyHelper).format("Hello, {}!", "Bob");
    verify(spyHelper).doubleIt(5);
  }
}
