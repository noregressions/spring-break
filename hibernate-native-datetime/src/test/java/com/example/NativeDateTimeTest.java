package com.example;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the Hibernate native query DateTime type change.
 *
 * BREAKING CHANGE in Spring Boot 4.0 (Hibernate 7.x):
 *
 * Hibernate 6.x returns java.sql.Date / java.sql.Timestamp from native SQL queries.
 * Hibernate 7.x returns java.time.LocalDate / java.time.LocalDateTime instead.
 *
 * This is a Tier 3 "Wrong Results" breaking change: the code compiles and starts,
 * but produces wrong results at runtime due to type mismatches. Code that casts
 * to java.sql.Date throws ClassCastException or fails silently.
 *
 * Only native queries are affected. JPQL queries and mapped entity queries are NOT affected
 * because Hibernate knows the entity types.
 *
 * References:
 * - Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
 * - Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 *
 * On Boot 3.5 (Hibernate 6.x): native query returns java.sql.Date → passes
 * On Boot 4.0 (Hibernate 7.x): native query returns java.time.LocalDate → fails
 */
@SpringBootTest
@Transactional
public class NativeDateTimeTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void nativeQueryDateShouldReturnSqlType() {
        // Create and persist an Event
        Event event = new Event();
        event.setName("Conference");
        event.setEventDate(LocalDate.of(2026, 6, 30));
        entityManager.persist(event);
        entityManager.flush();

        // Query the raw date column via native SQL
        Object result = entityManager.createNativeQuery(
            "SELECT event_date FROM event WHERE id = " + event.getId()
        ).getSingleResult();

        // Hibernate 6.x: returns java.sql.Date
        // Hibernate 7.x: returns java.time.LocalDate
        assertTrue(
            result instanceof java.sql.Date,
            "Expected java.sql.Date but got " + result.getClass().getName() +
            ". Hibernate 7 returns java.time types from native queries. " +
            "Code that casts to java.sql.Date will fail silently."
        );
    }

    @Test
    void entityManagerQueryDateReturnsCorrectType() {
        // Create and persist an Event
        Event event = new Event();
        event.setName("Workshop");
        event.setEventDate(LocalDate.of(2026, 7, 15));
        entityManager.persist(event);
        entityManager.flush();

        // Using EntityManager.find() or JPQL queries returns the correct type
        // This is NOT affected by the breaking change
        Event retrieved = entityManager.find(Event.class, event.getId());

        assertNotNull(retrieved);
        assertEquals(LocalDate.of(2026, 7, 15), retrieved.getEventDate());
        assertTrue(retrieved.getEventDate() instanceof LocalDate);
    }
}
