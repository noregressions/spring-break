# Hibernate Native Query DateTime Type Change Migration Test

## One-Line Summary
Hibernate 7.0 (Spring Boot 4.0) returns `java.time.LocalDate` from native SQL queries instead of `java.sql.Date`, silently breaking casts and type-dependent logic.

## What Breaks

Hibernate 7.0 bundled with Spring Boot 4.0 modernizes the return types for native SQL query results. When querying DATE columns via native SQL, the return type changes silently:

**Spring Boot 3.5.14 (Hibernate 6.x):** `SELECT date_column` → `java.sql.Date` object
**Spring Boot 4.0 (Hibernate 7.x):** `SELECT date_column` → `java.time.LocalDate` object

This is a **silent breaking change** that manifests only when executing native SQL queries. The application compiles and starts normally, but code that:
- Casts results to `java.sql.Date` throws `ClassCastException`
- Assumes type is `java.sql.Date` for instanceof checks
- Passes results to methods expecting `java.sql.Date`

These all fail or produce incorrect behavior.

## How This Test Works

The test suite uses Hibernate's `EntityManager` with native SQL queries to verify return types:

1. **nativeQueryDateShouldReturnSqlType()**: Executes a native query selecting a DATE column and asserts the result is `java.sql.Date` (Spring Boot 3.x behavior)
2. **entityManagerQueryDateReturnsCorrectType()**: Executes a regular JPQL query (not affected) and verifies type is `java.time.LocalDate`

The first test reveals the breaking change by checking return type with `instanceof`. The second test confirms that mapped entity queries are not affected.

## On Spring Boot 3.5.14 (Hibernate 6.x)

```bash
mvn test
```

Both tests pass. Example output:
```
✓ nativeQueryDateShouldReturnSqlType
  Expected java.sql.Date and got java.sql.Date
  
✓ entityManagerQueryDateReturnsCorrectType
  Retrieved Event with eventDate as LocalDate — correct
```

## On Spring Boot 4.0 (Hibernate 7.x)

```bash
mvn test
```

First test fails. Example failure:
```
✗ nativeQueryDateShouldReturnSqlType
  Expected java.sql.Date but got java.time.LocalDate
  Hibernate 7 returns java.time types from native queries.
  Code that casts to java.sql.Date will fail silently.

✓ entityManagerQueryDateReturnsCorrectType
  JPQL queries are not affected
```

## Why It Changed

Hibernate 7 modernised date/time handling to use Java 8+ `java.time` types (`LocalDate`, `LocalDateTime`, `Instant`) instead of legacy JDBC types (`java.sql.Date`, `java.sql.Timestamp`). This aligns with modern Java practices and provides better precision and flexibility.

However, native SQL queries are the exception: they bypass Hibernate's ORM layer, so Hibernate must infer types from the database column types. In Hibernate 7, this inference changed to return `java.time` types.

## Fix / Migration Path

### Option 1: Cast to java.time Types (Recommended)

Update casts to expect `java.time.LocalDate` / `java.time.LocalDateTime`:

```java
// Before (Hibernate 6.x)
Object result = entityManager.createNativeQuery(
    "SELECT event_date FROM event WHERE id = ?"
).getSingleResult();
java.sql.Date sqlDate = (java.sql.Date) result;  // Works on 6.x

// After (Hibernate 7.x)
Object result = entityManager.createNativeQuery(
    "SELECT event_date FROM event WHERE id = ?"
).getSingleResult();
java.time.LocalDate localDate = (java.time.LocalDate) result;  // Works on 7.x
```

### Option 2: Specify Result Class Explicitly

Tell Hibernate what type to return via the overloaded `createNativeQuery()`:

```java
// Explicitly specify result class (works on both 6.x and 7.x)
Object result = entityManager.createNativeQuery(
    "SELECT event_date FROM event WHERE id = ?",
    java.time.LocalDate.class  // Hibernate returns LocalDate
).setParameter(1, eventId).getSingleResult();

java.time.LocalDate date = (java.time.LocalDate) result;
```

### Option 3: Avoid Native Queries (Best Practice)

Replace native SQL with JPQL or Spring Data JPA repository methods. This avoids the breaking change entirely and is more maintainable:

```java
// Before (native SQL — affected by breaking change)
Object result = entityManager.createNativeQuery(
    "SELECT event_date FROM event WHERE id = ?"
).setParameter(1, eventId).getSingleResult();
java.sql.Date date = (java.sql.Date) result;

// After (JPQL — not affected, type is known)
Event event = entityManager.createQuery(
    "SELECT e FROM event e WHERE e.id = ?1",
    Event.class
).setParameter(1, eventId).getSingleResult();
java.time.LocalDate date = event.getEventDate();  // Type is LocalDate from entity
```

Or using Spring Data JPA:

```java
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // No native query, Hibernate ORM handles type mapping
    @Query("SELECT e FROM Event e WHERE e.id = ?1")
    Event findById(Long id);
}
```

### Option 4: Conditional Type Handling

If you support both versions temporarily, check the type at runtime:

```java
Object result = entityManager.createNativeQuery(
    "SELECT event_date FROM event WHERE id = ?"
).setParameter(1, eventId).getSingleResult();

java.time.LocalDate localDate;
if (result instanceof java.sql.Date) {
    // Hibernate 6.x
    localDate = ((java.sql.Date) result).toLocalDate();
} else if (result instanceof java.time.LocalDate) {
    // Hibernate 7.x
    localDate = (java.time.LocalDate) result;
} else {
    throw new IllegalArgumentException("Unexpected date type: " + result.getClass());
}
```

## Scope of Change

This breaking change **ONLY affects native SQL queries**:

```java
// AFFECTED — breaks on Hibernate 7.x
entityManager.createNativeQuery("SELECT date_col FROM table")

// NOT affected — works on both
entityManager.find(Entity.class, id)
entityManager.createQuery("SELECT e FROM Entity e")
event.getDateField()  // Returns LocalDate on both versions
```

## Audit Checklist

Search your codebase for native query patterns:

```bash
# Find native queries
grep -r "createNativeQuery" --include="*.java" src/

# Find casts to old JDBC types
grep -r "java.sql.Date\|java.sql.Timestamp" --include="*.java" src/
grep -r "(Date)\|(Timestamp)" --include="*.java" src/ | grep -i native
```

## References

- Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
- Java Time API: https://docs.oracle.com/javase/tutorial/datetime/

