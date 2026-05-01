# Hibernate CascadeType.SAVE_UPDATE Removed

Hibernate-specific CascadeType.SAVE_UPDATE removed; use JPA standard PERSIST/MERGE instead.

## What Breaks

`org.hibernate.annotations.CascadeType.SAVE_UPDATE` is removed in Hibernate 7.0. This was a Hibernate-specific cascade type (not in the JPA specification) that was deprecated in Hibernate 6.x.

**Code that accesses this enum constant fails at runtime:**
```java
// This fails on Hibernate 7.0 (Spring Boot 4.0)
org.hibernate.annotations.CascadeType.SAVE_UPDATE  // NoSuchFieldError
```

If this constant appears in an annotation on an entity that is loaded at startup, the application fails to initialize. If it's in dead code, the failure is delayed until that code path executes.

**Example annotation that breaks:**
```java
@OneToMany(cascade = {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
private List<Child> children;
```

Hibernate 7 removed this Hibernate-specific extension because JPA has standard cascade types (PERSIST, MERGE) that serve the same purpose.

## How This Test Works

The test uses reflection to verify the enum constant exists:

- **cascadeTypeAllShouldWork()**: Loads the Parent entity class (uses standard JPA CascadeType.ALL). Works identically on both versions.
- **saveUpdateCascadeTypeShouldExist()**: Uses reflection to load org.hibernate.annotations.CascadeType and check if SAVE_UPDATE constant exists. Passes on Hibernate 6.x, fails on 7.x.

## On Spring Boot 3.5.14

```bash
mvn clean test
```

Output: Both tests pass. SAVE_UPDATE exists.

## On Spring Boot 4.0

The second test fails:
```
NoSuchFieldException: SAVE_UPDATE not found on org.hibernate.annotations.CascadeType
```

Any entity using SAVE_UPDATE will fail to load, preventing application startup.

## Fix / Migration Path

Replace Hibernate-specific CascadeType with JPA standard types:

**Mapping for migration:**

| Hibernate-Specific | JPA Standard | Usage |
|---|---|---|
| SAVE_UPDATE | PERSIST + MERGE | New + detached entities |
| SAVE_UPDATE | ALL | All operations (most common) |

**Before (Hibernate 6.x):**
```java
@OneToMany(cascade = {org.hibernate.annotations.CascadeType.SAVE_UPDATE}, 
           mappedBy = "parent")
private List<Child> children;
```

**After (Hibernate 7.x / Spring Boot 4.0):**

Option 1 (recommended — most common):
```java
@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
private List<Child> children;
```

Option 2 (fine-grained):
```java
@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "parent")
private List<Child> children;
```

**Audit and migrate:**
```bash
# Find all uses of Hibernate-specific cascade types
grep -r "CascadeType\.SAVE_UPDATE" src/
grep -r "CascadeType\.DELETE_ORPHAN" src/
# Replace with JPA equivalents
```

## References

- Hibernate 7.0 Migration Guide: https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html
- Hibernate ORM 7.0 Release: https://docs.hibernate.org/orm/7.0/migration-guide/migration-guide.html
- JPA CascadeType (Jakarta Persistence): https://docs.oracle.com/javaee/7/api/javax/persistence/CascadeType.html
