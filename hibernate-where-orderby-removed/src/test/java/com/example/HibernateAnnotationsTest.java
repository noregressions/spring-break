package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.51 — Hibernate @Where and @OrderBy removed */
public class HibernateAnnotationsTest {

    @Test
    void annotationsShouldExistOnBoot35() {
        assertDoesNotThrow(
            () -> Class.forName("org.hibernate.annotations.Where"),
            "@Where should be on classpath in Hibernate 6.x (Boot 3.5)"
        );
        assertDoesNotThrow(
            () -> Class.forName("org.hibernate.annotations.OrderBy"),
            "@OrderBy should be on classpath in Hibernate 6.x (Boot 3.5)"
        );
    }
}
