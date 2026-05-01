# Hibernate Session.delete() Removed (Tier 1: Won't Launch)

**Summary**: Hibernate 7.0 (in Boot 4.0) removes `Session.delete(Object)`. Code using this method throws `NoSuchMethodError` at runtime.

## What Breaks

Hibernate 7.0 **removed** the deprecated `Session.delete(Object)` method. Code using native Hibernate Session API directly will fail:

1. **Session.delete() removed** — `session.delete(entity)` throws `NoSuchMethodError`
2. **Replacement available** — Use `session.remove(entity)` instead (available in both 6.x and 7.x)
3. **Affects native Session users** — Spring Data JPA users (using `EntityManager`) are unaffected

This is a **Tier 1 failure**: runtime `NoSuchMethodError` on first call to `delete()`.

## How This Test Works

The test module uses reflection to check whether Hibernate Session API methods exist:

- **SessionDeleteTest.java**: Two tests using reflection to verify method availability:
  - `sessionRemoveMethodShouldExist()` — checks `Session.remove(Object)` (exists on both versions)
  - `sessionDeleteMethodShouldExist()` — checks `Session.delete(Object)` (exists on 6.x, removed on 7.x)
- **App.java**: Minimal Spring Boot application

## On Spring Boot 3.5.14

```bash
mvn clean test
```

**Result**: ✓ Both tests pass. `Session.delete()` method exists.

```
[INFO] Tests run: 2, Failures: 0, Errors: 0
[INFO] sessionRemoveMethodShouldExist ✓
[INFO] sessionDeleteMethodShouldExist ✓
```

## On Spring Boot 4.0

```bash
mvn clean test
```

**Result**: ✗ First test passes, second test fails.

```
[ERROR] sessionRemoveMethodShouldExist ✓
[ERROR] sessionDeleteMethodShouldExist ✗
[ERROR] java.lang.NoSuchMethodException: org.hibernate.Session.delete(Object)
```

## Fix / Migration Path

### 1. Replace Session.delete() with Session.remove()

Both methods exist on Hibernate 6.x; only `remove()` exists on 7.x:

```java
// OLD (Hibernate 6.x)
session.delete(entity);

// NEW (works on both Hibernate 6.x and 7.x)
session.remove(entity);
```

### 2. For Spring Data JPA Users (Recommended)

If using Spring Data JPA, use `repository.delete()` or `EntityManager.remove()` instead of native Session:

```java
// Use Spring Data JPA repository
@Autowired
private EntityRepository repository;

// This works on all versions
repository.delete(entity);

// Or use EntityManager (also works on all versions)
@Autowired
private EntityManager entityManager;

entityManager.remove(entityManager.merge(entity));
```

### 3. Find All delete() Calls

Search your codebase for direct `Session.delete()` usage:

```bash
grep -r "\.delete(" --include="*.java" src/ | grep -i session
```

## References

- [Hibernate 7.0 Migration Guide](https://docs.jboss.org/hibernate/orm/7.0/migration-guide/migration-guide.html)
- [Hibernate ORM 7.0 Release](https://docs.hibernate.org/orm/7.0/migration-guide/migration-guide.html)
- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)

```java
// EntityManager.remove() works on both versions
entityManager.remove(entity);
```

This is the recommended approach and is **not affected** by this breaking change.

## Migration Path

1. **Search for `session.delete()` calls:**
   ```bash
   grep -r "session\.delete(" --include="*.java" src/
   ```

2. **Replace with `session.remove()`:**
   ```java
   // Before
   session.delete(entity);

   // After
   session.remove(entity);
   ```

3. **Or better yet, use EntityManager:**
   ```java
   @Autowired
   private EntityManager entityManager;

   // ...
   entityManager.remove(entity);
   ```

## Testing

This module includes a test that checks if the `delete` method exists on Hibernate Session:
- **Boot 3.5.14 (Hibernate 6.x)**: Test passes — `delete()` method is present
- **Boot 4.0.6 (Hibernate 7.x)**: Test fails — `delete()` method is removed

Run:
```bash
mvn test                                    # Boot 3.5.14 — passes
mvn test -Dspring-boot.version=4.0.6       # Boot 4.0 — fails
```

## Impact Level

**Tier 1 — Won't Launch**

The application compiles without error, but fails at runtime if any code path attempts to call `session.delete()`.
This primarily affects code using native Hibernate Session directly, not Spring Data JPA.
