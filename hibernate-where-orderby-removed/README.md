# Hibernate @Where and @OrderBy Removed (Tier 1: Won't Compile)

**Summary**: The Hibernate-specific `@Where` and `@OrderBy` annotations have been removed in Hibernate 7.0. They have been replaced by `@SQLRestriction` and `@SQLOrder` (or JPA's standard `@OrderBy`).

## What breaks

In Hibernate 6.x (Spring Boot 3.5), Hibernate provided `@org.hibernate.annotations.Where` and `@org.hibernate.annotations.OrderBy` for defining SQL-level filters and ordering for entities and collections.

In Hibernate 7.0 (Spring Boot 4.0), these annotations are removed. Entities using them will fail to compile.

```java
// Spring Boot 3.5 / Hibernate 6.x (Works)
@Entity
public class User {
    @OneToMany
    @Where(clause = "deleted = false")
    @OrderBy(clause = "name desc")
    private List<Post> posts;
}

// Spring Boot 4.0 / Hibernate 7.0 (Compilation Error)
// cannot find symbol: class Where
// cannot find symbol: class OrderBy
```

## How this test works

The module `hibernate-where-orderby-removed` contains:
- `User.java`: An entity using the removed annotations.
- `Post.java`: The target entity of the association.
- `HibernateAnnotationsTest.java`: A test verifying the existence of these annotations.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile with "cannot find symbol" errors for the annotations.

## Fix / Migration Path

- Replace `@Where(clause = "...")` with `@SQLRestriction("...")`.
- Replace `@OrderBy(clause = "...")` with `@SQLOrder("...")` or the standard JPA `@jakarta.persistence.OrderBy`.

```java
// Spring Boot 4.0 (Fixed)
@Entity
public class User {
    @OneToMany
    @SQLRestriction("deleted = false")
    @SQLOrder("name desc")
    private List<Post> posts;
}
```

## References

- [Hibernate 7.0 Migration Guide](https://docs.hibernate.org/orm/7.0/migration-guide/migration-guide.html) — Annotations
- Master list entry: 1.51
