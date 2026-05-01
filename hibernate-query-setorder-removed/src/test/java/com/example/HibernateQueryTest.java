package com.example;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Master list: 1.52 — Hibernate SelectionQuery.setOrder removed */
@SpringBootTest
@Transactional
public class HibernateQueryTest {

    @SpringBootApplication
    static class TestApp {}

    @Autowired
    private EntityManager entityManager;

    @Test
    void setOrderMethodShouldExistOnBoot35() {
        Session session = entityManager.unwrap(Session.class);
        List<Product> products = HibernateQueryUsage.getOrderedProducts(session);
        assertNotNull(products);
    }

    @Test
    void setOrderIsDiscoverableViaReflection() {
        boolean methodExists = false;
        try {
            // Check for SelectionQuery.setOrder(List)
            Method method = SelectionQuery.class.getMethod("setOrder", List.class);
            methodExists = true;
        } catch (NoSuchMethodException e) {
            methodExists = false;
        }

        assertTrue(methodExists, "setOrder(List) should be available on SelectionQuery in Hibernate 6.x (Boot 3.5)");
    }
}
