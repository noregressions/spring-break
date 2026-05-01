# Spring Batch In-Memory Default (Tier 2: Won't Run)

**Summary**: Spring Boot 4.0 (Spring Batch 6.0) defaults to an in-memory (resourceless) job repository. JDBC-based metadata storage is no longer auto-configured by the default `spring-boot-starter-batch`.

## What breaks

In Spring Boot 3.5, including `spring-boot-starter-batch` and a `DataSource` would automatically configure a JDBC-based `JobRepository` and initialize the Batch metadata tables (if configured).

In Spring Boot 4.0, the JDBC support was split into a separate module. The default `spring-boot-starter-batch` now only provides in-memory support. Consequently:
1. `spring.batch.jdbc.*` properties are ignored.
2. No Batch metadata tables are created in the database.
3. Job metadata is lost when the application restarts.

```properties
# Ignored in Spring Boot 4.0 without the -jdbc starter
spring.batch.jdbc.initialize-schema=always
```

## How this test works

The module `batch-in-memory-default` contains:
- `BatchApp.java`: A standard Spring Batch application.
- `application.properties`: Sets `spring.batch.jdbc.initialize-schema=always`.
- `BatchSchemaTest.java`: A test that checks if the `BATCH_JOB_INSTANCE` table exists in the H2 database.

On Boot 3.5: The table exists, and the test passes.
On Boot 4.0: The table does not exist because the JDBC infrastructure is missing. The test fails with an assertion error.

## Fix / Migration Path

Explicitly add the `spring-boot-starter-batch-jdbc` dependency to restore the previous behavior.

```xml
<!-- Spring Boot 4.0 (Fixed) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch-jdbc</artifactId>
</dependency>
```

## References

- [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) — Spring Batch
- Master list entry: 2.6
