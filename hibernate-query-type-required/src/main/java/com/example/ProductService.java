package com.example;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Demonstrates Hibernate 7's stricter untyped query validation.
 *
 * Hibernate 6.x (Boot 3.5): Session.createQuery(String) with joins and no
 *   explicit result type is tolerated. The query silently returns List<Object[]>.
 *
 * Hibernate 7.x (Boot 4.0): Queries with joins and no explicit result type are
 *   considered ambiguous. Hibernate throws SemanticException at runtime:
 *   "Query has no explicit SELECT and multiple query roots are defined"
 *
 * Fix: Add result type — session.createQuery(hql, Product.class)
 *
 * References:
 * - Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final EntityManager entityManager;

    public ProductService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Untyped join query — no result type specified.
     *
     * On Boot 3.5 (Hibernate 6.6): runs fine, returns List of Object[]
     * On Boot 4.0 (Hibernate 7.x): throws SemanticException — ambiguous query
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public List<Object[]> findProductsWithCategory() {
        Session session = entityManager.unwrap(Session.class);
        // Join query without result type — ambiguous on Hibernate 7
        return session.createQuery(
            "from Product p join p.category c"
        ).getResultList();
    }
}
