package com.example;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import java.util.ArrayList;
import java.util.List;

/**
 * Category (a) — Won't Compile on 4.0
 *
 * Uses the Hibernate-specific CascadeType.SAVE_UPDATE.
 *
 * Hibernate 6.x (Boot 3.5): org.hibernate.annotations.CascadeType.SAVE_UPDATE
 *   exists (deprecated). Persisting/merging a Parent cascades to its Children.
 *
 * Hibernate 7.0 (Boot 4.0): SAVE_UPDATE removed from the enum.
 *   This file fails to compile:
 *     error: cannot find symbol
 *       symbol:   variable SAVE_UPDATE
 *       location: class org.hibernate.annotations.CascadeType
 *
 * Fix: Replace with JPA standard cascades:
 *   @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "parent")
 *
 * Note: SAVE_UPDATE had Hibernate-specific semantics around detached entity
 * reattachment. PERSIST + MERGE is the closest JPA equivalent but not identical
 * for all edge cases. Test thoroughly after migrating.
 *
 * References:
 * - Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
 */
@Entity
@Table(name = "parent_entity")
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // BREAKING: CascadeType.SAVE_UPDATE is Hibernate-specific.
    // Deprecated in Hibernate 6.x, REMOVED in Hibernate 7.0.
    // This @Cascade annotation won't compile on Boot 4.0.
    @OneToMany(mappedBy = "parent")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private List<Child> children = new ArrayList<>();

    public Parent() {
    }

    public Parent(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }
}
