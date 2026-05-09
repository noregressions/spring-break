# `@JsonComponent` Renamed to `@JacksonComponent` (Tier 1: Won't Build)

**Summary**: Spring Boot 4.0 renames Spring Boot's `@JsonComponent` and `@JsonMixin` annotations to `@JacksonComponent` and `@JacksonMixin` to align with Jackson 3 branding. The annotations also moved jar (`spring-boot` core → `spring-boot-jackson`, the new per-concern module). The package stays the same: `org.springframework.boot.jackson.*`. Code that imports the old names fails to compile.

## What Breaks

| | Boot 3.5.14 | Boot 4.0.6 |
|---|---|---|
| `@JsonComponent` | `org.springframework.boot.jackson.JsonComponent` in `spring-boot-3.5.14.jar` | absent |
| `@JsonMixin` | `org.springframework.boot.jackson.JsonMixin` in `spring-boot-3.5.14.jar` | absent |
| `@JacksonComponent` | absent | `org.springframework.boot.jackson.JacksonComponent` in `spring-boot-jackson-4.0.6.jar` |
| `@JacksonMixin` | absent | `org.springframework.boot.jackson.JacksonMixin` in `spring-boot-jackson-4.0.6.jar` |

Empirically verified by jar inspection.

## How This Test Works

`CustomSerializerMarker` is annotated with `@JsonComponent` (Boot 3 import) and **deliberately does not extend a Jackson serializer class**. The cheat-sheet card's original `no_module_reason` claimed the rename couldn't be tested in isolation because Jackson 3 also moved `JsonSerializer` to a different package — that's true for any class that BOTH uses `@JsonComponent` AND extends `JsonSerializer`. By decoupling the two, we isolate the annotation-rename failure cleanly. The annotation alone is the load-bearing piece; the test proves only that.

## On Spring Boot 3.5.14

```bash
mvn test
```

**Result**: ✓ Compiles, runs, passes. The annotation resolves at `org.springframework.boot.jackson.JsonComponent`.

## On Spring Boot 4.0.6

```bash
mvn compile -Dspring-boot.version=4.0.6
```

**Result**: ✗ Compile fails.

**Error**:
```
CustomSerializerMarker.java:[3,40] package org.springframework.boot.jackson does not exist
CustomSerializerMarker.java:[33,2] cannot find symbol
```

## Fix / Migration Path

1. Rename the import: `JsonComponent` → `JacksonComponent`, `JsonMixin` → `JacksonMixin`. The package (`org.springframework.boot.jackson`) is unchanged.
2. Rename the annotation usages.
3. Add `spring-boot-jackson` (or use `spring-boot-starter-jackson`) if your build doesn't pull it in transitively. Most web apps get it via `spring-boot-starter-web` already.

```diff
- import org.springframework.boot.jackson.JsonComponent;
+ import org.springframework.boot.jackson.JacksonComponent;

- @JsonComponent
+ @JacksonComponent
  public class MoneySerializer extends JsonSerializer<Money> { ... }
```

## Watch Out

If your custom serializer also extends `JsonSerializer` from Jackson 2's package (`com.fasterxml.jackson.databind.JsonSerializer`), you'll hit a second compile error: Jackson 3 moved that class to `tools.jackson.databind.JsonSerializer`. Both renames need to land together. The Jackson 3 group-ID migration is covered separately by the `jackson-group-id` module/card.

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- Sibling modules: `jackson-group-id`, `jackson-class-renames`
