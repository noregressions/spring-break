package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the Hibernate dialect removal breaking change in Hibernate 7.0.
 *
 * BREAKING CHANGE: Version-specific dialect classes removed.
 *
 * Hibernate 6.x (Spring Boot 3.5):
 *   - MySQL8Dialect, PostgreSQL10Dialect, etc. exist (deprecated)
 *   - Application can set spring.jpa.properties.hibernate.dialect=...Dialect
 *
 * Hibernate 7.0 (Spring Boot 4.0):
 *   - All version-specific dialects removed
 *   - ClassNotFoundException if application.properties uses old dialect name
 *   - Hibernate auto-detects dialect from JDBC driver metadata
 *
 * IMPACT: Applications that explicitly configure versioned dialects fail at
 * startup before any code executes.
 *
 * MIGRATION: Remove explicit dialect configuration or use generic dialects
 * (MySQLDialect, PostgreSQLDialect) instead of version-specific ones.
 *
 * Reference: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
 */
@SpringBootTest
public class DialectTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testCreateAndRetrieveProduct() {
        Product product = new Product("Laptop", 999.99, 10);
        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName());
        assertEquals(999.99, saved.getPrice());
        assertEquals(10, saved.getQuantity());
    }

    @Test
    public void testFindProductById() {
        Product product = new Product("Mouse", 29.99, 50);
        Product saved = productRepository.save(product);

        Product found = productRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("Mouse", found.getName());
    }

    /**
     * BREAKING CHANGE TEST: Verify version-specific dialect class is loadable.
     *
     * Many production applications have application.properties like:
     *   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
     *
     * Hibernate 6.x: MySQL8Dialect exists (deprecated) — class loads successfully
     * Hibernate 7.0: MySQL8Dialect removed — ClassNotFoundException at startup
     *
     * This breaks applications silently because the class name is a string in
     * properties, not a Java import. The error only appears at runtime when
     * Hibernate tries to instantiate the dialect during context initialization.
     *
     * MIGRATION: Remove explicit dialect configuration or replace with generic
     * dialect (MySQLDialect, PostgreSQLDialect, etc.).
     *
     * Reference: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
     *
     * NOTE: If this test fails on BOTH versions, the specific class name may
     * need adjusting. Try:
     *   - org.hibernate.dialect.MySQL8Dialect (common, removed in 7.0)
     *   - org.hibernate.dialect.PostgreSQL10Dialect
     *   - org.hibernate.dialect.MariaDB106Dialect
     */
    @Test
    public void versionSpecificDialectClassShouldBeLoadable() {
        // MySQL8Dialect — commonly set explicitly in application.properties.
        // Deprecated in Hibernate 6.x, removed in Hibernate 7.0.
        assertDoesNotThrow(
                () -> Class.forName("org.hibernate.dialect.MySQL8Dialect"),
                "MySQL8Dialect should be loadable on Hibernate 6.x (Boot 3.x). "
                + "On Hibernate 7.x (Boot 4.0), this class is removed. "
                + "Use automatic dialect detection instead of explicit dialect configuration."
        );
    }
}
