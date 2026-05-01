package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Master list: 1.39 — MockitoTestExecutionListener removed.
 *
 * In Spring Boot 3.5, @SpringBootTest automatically triggers MockitoTestExecutionListener
 * which initializes @Mock fields.
 * In Spring Boot 4.0, this listener is gone, so @Mock fields remain null unless
 * @ExtendWith(MockitoExtension.class) is added.
 */
@SpringBootTest
class MockitoListenerTest {

    @Mock
    private MyService myService;

    @Test
    void mockShouldBeInitializedByBoot() {
        assertNotNull(myService,
            "In Spring Boot 3.5, @Mock fields are initialized automatically by MockitoTestExecutionListener. " +
            "In 4.0, this listener is removed, and fields stay null unless the MockitoExtension is used.");

        when(myService.greet()).thenReturn("Hello");
        org.junit.jupiter.api.Assertions.assertEquals("Hello", myService.greet());
    }
}
