package com.example;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.53 — Hibernate EmptyInterceptor removed */
public class HibernateInterceptorTest {

    @Test
    void emptyInterceptorShouldExistOnBoot35() {
        // Direct usage — fails to compile on Boot 4.0
        MyHibernateInterceptor interceptor = new MyHibernateInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    void emptyInterceptorIsLoadableViaReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.hibernate.EmptyInterceptor"),
            "EmptyInterceptor should be on classpath in Hibernate 6.x (Boot 3.5)"
        );
    }
}
