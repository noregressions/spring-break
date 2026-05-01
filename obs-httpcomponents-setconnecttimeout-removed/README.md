# HttpComponents setSocketTimeout removed (Tier 1: Won't Compile)

**Summary**: The `setSocketTimeout(int)` method on `RequestConfig.Builder` was removed in Apache HttpClient 5.0 (replaced by `setResponseTimeout`). This impacts users migrating from Spring Boot 2.x (which used HttpClient 4.x).

## What breaks

In Spring Boot 2.x (HttpClient 4.x), timeouts were configured using `int`:

```java
RequestConfig.custom()
    .setSocketTimeout(5000)
    .build();
```

In HttpClient 5.x (used in Spring Boot 3.x and 4.0), this method is removed. Code calling it will fail to compile.

## How this test works

The module contains `HttpComponentsUsage.java` which calls the removed `setSocketTimeout(int)` method.

- On Boot 3.5: Fails to compile (demonstrating the breaking change from 4.x to 5.x).
- On Boot 4.0: Fails to compile.

## Fix / Migration Path

Use the new `setResponseTimeout(Timeout)` method:

```java
import org.apache.hc.core5.util.Timeout;

RequestConfig.custom()
    .setResponseTimeout(Timeout.ofMilliseconds(5000))
    .build();
```

## References

- [Apache HttpClient 5.0 Migration Guide](https://hc.apache.org/httpcomponents-client-5.0.x/migration-guide/index.html)
- Master list entry: 1.34
