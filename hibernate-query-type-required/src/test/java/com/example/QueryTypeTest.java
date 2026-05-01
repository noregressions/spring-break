package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Hibernate's handling of untyped join queries.
 *
 * Hibernate 6.x (Boot 3.5): Session.createQuery("from Product p join p.category c")
 *   without an explicit result type silently returns List of Object[]. Test passes.
 *
 * Hibernate 7.x (Boot 4.0): The same query is considered ambiguous because there
 *   are multiple query roots (Product + Category from the join) and no explicit
 *   SELECT or result type. Throws SemanticException at runtime.
 *
 * This is a Tier 2 failure: compiles on both, runtime exception on 4.0.
 *
 * References:
 * - Hibernate 7.0 Migration Guide — "Query with Implicit SELECT and No Explicit Result Type"
 */
@SpringBootTest
@Transactional
public class QueryTypeTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductService productService;

    @Test
    void untypedJoinQueryShouldWork() {
        // Seed data
        Category electronics = new Category("Electronics");
        entityManager.persist(electronics);

        Product laptop = new Product("Laptop", 999.99, electronics);
        entityManager.persist(laptop);
        entityManager.flush();

        // On Boot 3.5: join query without result type returns List<Object[]>
        // On Boot 4.0: throws SemanticException — ambiguous query
        List<Object[]> results = productService.findProductsWithCategory();

        assertNotNull(results);
        assertEquals(1, results.size());
    }
}
