package com.example;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (a) — Won't Compile on 4.0
 *
 * Tests that the Parent entity (annotated with @Cascade(CascadeType.SAVE_UPDATE))
 * compiles and loads on Boot 3.5.
 *
 * On Boot 3.5: Parent.java compiles (SAVE_UPDATE deprecated but present).
 *   Entity loads, persistence works — tests pass.
 *
 * On Boot 4.0: Parent.java fails to compile because CascadeType.SAVE_UPDATE
 *   is removed from the Hibernate enum. These tests never run.
 *
 * The compilation failure in the entity class IS the test. These test methods
 * just verify that the entity is functional on the baseline version.
 *
 * Fix: Replace @Cascade(CascadeType.SAVE_UPDATE) with JPA standard:
 *   @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "parent")
 *
 * References:
 * - Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
 */
@SpringBootTest
@Transactional
public class CascadeTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void parentEntityWithSaveUpdateCascadeCompiles() {
        // The real test is that Parent.java compiles with
        // @Cascade(CascadeType.SAVE_UPDATE). On Boot 4.0, compilation fails
        // before this test ever runs.
        Parent parent = new Parent("Alice");
        entityManager.persist(parent);
        entityManager.flush();

        assertNotNull(parent.getId(),
                "Parent entity should persist successfully");
    }

    @Test
    void parentChildRelationshipWorks() {
        // Persist parent and children separately — we're testing that the
        // entity graph loads, not the cascade behaviour itself.
        Parent parent = new Parent("Bob");
        entityManager.persist(parent);

        Child child1 = new Child("Child 1");
        child1.setParent(parent);
        entityManager.persist(child1);

        Child child2 = new Child("Child 2");
        child2.setParent(parent);
        entityManager.persist(child2);

        entityManager.flush();
        entityManager.clear();

        Parent found = entityManager.find(Parent.class, parent.getId());
        assertNotNull(found);
        assertEquals(2, found.getChildren().size(),
                "Parent should have 2 children");
    }
}
