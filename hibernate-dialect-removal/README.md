# Hibernate Dialect Removal

Version-specific database dialects removed in Hibernate 7.0; use auto-detection or generic dialects.

## What Breaks

Hibernate 7.0 (Spring Boot 4.0) removes all version-specific dialect classes. Applications with explicit dialect configuration in application.properties fail at startup with ClassNotFoundException.

**Removed dialects (Hibernate 7.0):**
- MySQL57Dialect, MySQL8Dialect
- PostgreSQL96Dialect, PostgreSQL10Dialect, PostgreSQL11Dialect
- Oracle10gDialect, Oracle12cDialect
- SQLServer2008Dialect, SQLServer2012Dialect
- And many others (all version-specific variants)

**Configuration that breaks on Spring Boot 4.0:**
```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**Error on startup:**
```
ClassNotFoundException: org.hibernate.dialect.MySQL8Dialect
  at Hibernate initialization
```

Hibernate now auto-detects the database dialect from JDBC driver metadata, making explicit dialect configuration unnecessary in most cases.

## How This Test Works

The test combines CRUD operations with a reflection check:

- **testCreateAndRetrieveProduct()** / **testFindProductById()**: Verify basic JPA operations work with auto-detected dialect (H2 in-memory database).
- **versionSpecificDialectClassShouldBeLoadable()**: Uses reflection to load MySQL8Dialect. Passes on Hibernate 6.x (class exists), fails on Hibernate 7.x (class removed).

## On Spring Boot 3.5.14

```bash
mvn clean test
```

Output: All tests pass. Version-specific dialects exist and are loadable.

## On Spring Boot 4.0

CRUD tests pass (H2 auto-detection works). The reflection test fails:
```
ClassNotFoundException: org.hibernate.dialect.MySQL8Dialect
```

If application.properties explicitly configures a version-specific dialect, the application fails to start before any tests run.

## Fix / Migration Path

**Option 1: Remove explicit dialect (recommended)**

Delete any lines like these from application.properties:
```properties
# REMOVE:
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL10Dialect
```

Hibernate auto-detects from the JDBC driver:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
# No explicit dialect needed — Hibernate detects MySQL automatically
```

**Option 2: Use generic (version-agnostic) dialects**

If you must specify a dialect:
```properties
# Generic dialects that work across major versions:
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

**Audit and migrate:**
```bash
# Find all explicit dialect configurations
grep -r "hibernate\.dialect" src/main/resources/
# Remove version-specific ones; replace with generic or remove entirely
```

## References

- Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
- Hibernate ORM 7.0 Release: https://docs.hibernate.org/orm/7.0/migration-guide/migration-guide.html
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
