# Hibernate SelectionQuery.setOrder Removed (Tier 1: Won't Compile)

**Summary**: The incubating `setOrder()` method in `SelectionQuery` (and `Query`) has been removed in Hibernate 7.0. It has been replaced by more robust sorting alternatives in `SelectionSpecification`.

## What breaks

Hibernate 6.x introduced an incubating `setOrder(List<Order>)` method to `SelectionQuery` as part of its Jakarta Data support.

In Hibernate 7.0 (Spring Boot 4.0), this method is removed. Code that uses it will fail to compile.

```java
// Spring Boot 3.5 / Hibernate 6.x (Works)
SelectionQuery<Product> query = session.createSelectionQuery("from Product", Product.class);
query.setOrder(Collections.singletonList(Order.asc(Product.class, "name")));

// Spring Boot 4.0 / Hibernate 7.0 (Compilation Error)
// cannot find symbol: method setOrder(java.util.List<org.hibernate.query.Order>)
```

## How this test works

The module `hibernate-query-setorder-removed` contains:
- `Product.java`: A simple JPA entity.
- `HibernateQueryUsage.java`: A class that calls `query.setOrder(...)`.
- `HibernateQueryTest.java`: A test that asserts the method works on 3.5.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile with a "cannot find symbol" error for `setOrder`.

## Fix / Migration Path

Migrate to using standard HQL `ORDER BY` clauses or the new `SelectionSpecification.sort()` API if applicable.

```java
// Spring Boot 4.0 (Recommended Fix: HQL)
SelectionQuery<Product> query = session.createSelectionQuery("from Product order by name asc", Product.class);
```

## References

- [Hibernate 7.0 Migration Guide](https://docs.hibernate.org/orm/7.0/migration-guide/migration-guide.html) — Removed Query#setOrder
- Master list entry: 1.52
