# HttpHeaders no longer MultiValueMap (Tier 1: Won't Compile)

**Summary**: In Spring Framework 7.0 (included in Spring Boot 4.0), the `HttpHeaders` class no longer implements the `MultiValueMap<String, String>` interface. This change was made to discourage treating HTTP headers as a general-purpose map and to improve performance by using more specialized header handling logic.

## What breaks

Code that treats an `HttpHeaders` instance as a `MultiValueMap` or `Map` will fail to compile. This includes passing it to methods that expect a `MultiValueMap`, or using `Map` methods like `containsKey()`, `keySet()`, or `entrySet()` directly on the `HttpHeaders` object.

```java
HttpHeaders headers = new HttpHeaders();
headers.containsKey("Content-Type"); // Fails in 4.0
MultiValueMap<String, String> map = headers; // Fails in 4.0
```

## How this test works

The module contains:
- `HttpHeadersUsage.java`: Attempts to treat `HttpHeaders` as a `MultiValueMap` and calls `containsKey()`, which are legal in 3.5 but fail in 4.0.
- `HttpHeadersTest.java`: Asserts that `HttpHeaders` behaves as a `MultiValueMap` on 3.5.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile because `HttpHeaders` no longer implements `MultiValueMap`.

## Fix / Migration Path

1. **Replace Map methods with HttpHeaders-specific methods**:
   - `containsKey(key)` -> `containsHeader(key)`
   - `keySet()` -> `headerNames()`
   - `entrySet()` -> `headerSet()`

2. **Convert to Map when necessary**:
   If you truly need a Map representation, use the new `asMultiValueMap()` (deprecated view) or `toSingleValueMap()` (copy) methods.

```java
// Migration example
if (headers.containsHeader("Content-Type")) { ... }
```

## References

- [Spring Framework 7.0 Release Notes](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes)
- Master list entry: 1.36
