# Hibernate EmptyInterceptor Removed (Tier 1: Won't Compile)

**Summary**: The `EmptyInterceptor` class has been removed in Hibernate 7.0. Since Hibernate 6.0, the `Interceptor` interface has used default methods for all its operations, making `EmptyInterceptor` redundant.

## What breaks

In Hibernate 6.x (Spring Boot 3.5), `EmptyInterceptor` was a convenient base class that provided empty implementations of all `Interceptor` methods.

In Hibernate 7.0 (Spring Boot 4.0), `EmptyInterceptor` is removed. Code that extends it or references it will fail to compile.

```java
// Spring Boot 3.5 / Hibernate 6.x (Works)
public class MyInterceptor extends EmptyInterceptor {
    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        return super.onLoad(entity, id, state, propertyNames, types);
    }
}

// Spring Boot 4.0 / Hibernate 7.0 (Compilation Error)
// cannot find symbol: class EmptyInterceptor
```

## How this test works

The module `hibernate-empty-interceptor-removed` contains:
- `MyHibernateInterceptor.java`: A class that extends `EmptyInterceptor`.
- `HibernateInterceptorTest.java`: A test that asserts `EmptyInterceptor` exists.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile with a "cannot find symbol" error for `EmptyInterceptor`.

## Fix / Migration Path

Implement the `Interceptor` interface directly. Since it now has default methods, you only need to override the methods you are interested in.

Note that some method signatures in `Interceptor` have also changed (e.g., `Serializable id` changed to `Object id` in 6.0, and some old deprecated signatures were removed in 7.0).

```java
// Spring Boot 4.0 (Fixed)
public class MyInterceptor implements Interceptor {
    @Override
    public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
        return true; // or your custom logic
    }
}
```

## References

- [Hibernate 7.0 Migration Guide](https://docs.hibernate.org/orm/7.0/migration-guide/migration-guide.html) — Interceptor / EmptyInterceptor
- Master list entry: 1.53
