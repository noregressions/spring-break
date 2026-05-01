package com.example;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Category (a) — Won't Compile on 4.0
 *
 * Demonstrates Hibernate Session.delete() removal.
 *
 * Hibernate 6.x (Boot 3.5): Session interface declares both delete() and remove().
 * Hibernate 7.0 (Boot 4.0):   Session.delete(Object) is removed from the interface.
 *
 * On Boot 3.5: compiles and passes — session.delete() works.
 * On Boot 4.0:   fails to compile — "cannot find symbol: method delete(Item)"
 *
 * Fix: Replace session.delete(entity) with session.remove(entity).
 *      Both methods exist on Hibernate 6.x, so the fix is backward-compatible.
 *
 * References:
 * - Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
 */
@SpringBootTest
@Transactional
public class SessionDeleteTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void deleteEntityUsingHibernateSession() {
        // Persist an item
        Item item = new Item("Widget", "A test widget");
        entityManager.persist(item);
        entityManager.flush();
        Long id = item.getId();
        assertNotNull(id);

        // Unwrap the Hibernate Session and call delete() directly.
        //
        // Session.delete(Object) was deprecated in Hibernate 6.x and
        // REMOVED in Hibernate 7.0. On Boot 4.0 this line won't compile:
        //   error: cannot find symbol
        //     symbol:   method delete(Item)
        //     location: interface org.hibernate.Session
        Session session = entityManager.unwrap(Session.class);
        session.delete(item);
        entityManager.flush();

        // Verify the item is gone
        assertNull(entityManager.find(Item.class, id),
                "Item should have been deleted via Session.delete()");
    }

    @Test
    void removeEntityStillWorks() {
        // Session.remove(Object) exists on BOTH Hibernate 6.x and 7.0.
        // This is the migration target — show developers the fix works now.
        Item item = new Item("Gadget", "Another test item");
        entityManager.persist(item);
        entityManager.flush();
        Long id = item.getId();

        Session session = entityManager.unwrap(Session.class);
        session.remove(item);
        entityManager.flush();

        assertNull(entityManager.find(Item.class, id),
                "Item should have been deleted via Session.remove()");
    }
}
