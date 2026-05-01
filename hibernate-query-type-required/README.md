# Hibernate Query Type Required (Tier 1: Won't Launch)

**Summary**: Hibernate 7.0 (in Boot 4.0) requires explicit result types on `createQuery()` calls. Untyped queries throw `IllegalArgumentException` at runtime.

## What Breaks

Hibernate 7.0 **removed** support for untyped `createQuery()` calls. All queries must specify a result type parameter at call time. This is a **Tier 1 failure**: queries fail at runtime on first execution.

1. **Untyped queries rejected** — `createQuery(jpqlString)` with no result type parameter throws exception
2. **Type safety enforced** — Hibernate 7 requires explicit `createQuery(jpqlString, resultType)`
3. **Runtime exception** — `java.lang.IllegalArgumentException: createQuery() requires a result type`

## How This Test Works

The test module demonstrates untyped vs. typed Hibernate queries:

- **QueryTypeTest.java**: Contains two tests:
  - `typedQueryShouldWork()` — uses `createQuery(jpql, Product.class)` (works on both Hibernate 6 & 7)
  - `untypedQueryShouldWork()` — uses `createQuery(jpql)` (works on 6, fails on 7)
- **Product.java**: Simple JPA entity
- **App.java**: Minimal Spring Boot application

## On Spring Boot 3.5.14

```bash
mvn clean test
```

**Result**: ✓ Both tests pass. Hibernate 6.x supports untyped queries.

```
[INFO] Tests run: 2, Failures: 0, Errors: 0
[INFO] testTypedQueryShouldWork ✓
[INFO] testUntypedQueryShouldWork ✓
```

## On Spring Boot 4.0

```bash
mvn clean test
```

**Result**: ✗ First test passes, second test fails at query execution.

```
[ERROR] testUntypedQueryShouldWork ✗
[ERROR] java.lang.IllegalArgumentException: createQuery() requires a result type
```

## Fix / Migration Path

### 1. Add Result Type Parameter

Replace all untyped `createQuery(jpql)` calls with typed `createQuery(jpql, resultType)`:

```java
// OLD (Hibernate 6.x)
Query query = entityManager.createQuery("SELECT p FROM Product p");
List results = query.getResultList();

// NEW (Hibernate 7.x)
Query query = entityManager.createQuery("SELECT p FROM Product p", Product.class);
List<Product> results = query.getResultList();
```

### 2. Use TypedQuery for Clarity

TypedQuery provides better compile-time type safety:

```java
// Alternative (also works on both versions)
TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p", Product.class);
List<Product> results = query.getResultList();
```

### 3. Handle Projections with Tuple or DTO

For SELECT queries with specific columns, use `Tuple` or a custom DTO:

```java
// Using Tuple
Query query = entityManager.createQuery(
    "SELECT p.id, p.name FROM Product p",
    Tuple.class
);
List<Tuple> results = query.getResultList();

// Using custom DTO
Query query = entityManager.createQuery(
    "SELECT new com.example.ProductDTO(p.id, p.name) FROM Product p",
    ProductDTO.class
);
List<ProductDTO> results = query.getResultList();
```

### 4. Find All Untyped Queries

Search your codebase for untyped query patterns:

```bash
grep -r "createQuery(" --include="*.java" src/ | grep -v ", *[A-Z]"
```

## References

- [Hibernate 7.0 Migration Guide](https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html)
- [Hibernate ORM 7.0 Release](https://docs.hibernate.org/orm/7.0/migration-guide/migration-guide.html)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
