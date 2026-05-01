# Spring Batch 6.0 Sequence Table Renames

## What Breaks

Spring Batch 6.0 (Spring Boot 4.0) renames multiple core schema table and sequence names. Applications with pre-initialized schemas using old names encounter schema mismatches at runtime.

**Key table/sequence renames:**
- BATCH_JOB_SEQ → BATCH_JOB_INSTANCE_SEQ
- BATCH_STEP_EXECUTION_SEQ → BATCH_STEP_EXECUTION_INSTANCE_SEQ

Applications that rely on schema auto-initialization are unaffected (Spring Boot 4.0 creates new names). Applications with:
- Hand-written DDL scripts using old names
- Pre-initialized production databases with old schema
- Database migration tools (Flyway/Liquibase) with old names
...will fail when Spring Batch 6.0 queries for the renamed tables, resulting in "table not found" errors at runtime.

## Module Contents

- **pom.xml**: Spring Boot 3.5.14 parent with `spring-boot-starter-batch` and h2
- **App.java**: Spring Boot application with `@EnableBatchProcessing` to initialize schema
- **application.properties**: H2 in-memory database, batch schema auto-initialization, job auto-run disabled for testing
- **BatchSchemaTest.java**: Tests for the old table names using JDBC metadata inspection

## How This Test Works

The test uses JDBC DatabaseMetaData to check for the existence of old Batch schema table names:

- **batchJobSeqTableShouldExist()**: Queries for BATCH_JOB_SEQ table. Spring Boot 3.5.14 auto-initializes with this name; Spring Boot 4.0 auto-initializes with BATCH_JOB_INSTANCE_SEQ instead.
- **batchStepExecutionSeqTableShouldExist()**: Queries for BATCH_STEP_EXECUTION_SEQ table. Same pattern: old name on 3.5.14, renamed on 4.0.

The test demonstrates the breaking change by verifying the old schema is present (passes on 3.5.14, fails on 4.0).

## On Spring Boot 3.5.14

```bash
mvn clean test
```

Output: Both tests pass. Schema auto-initializes with old table names (BATCH_JOB_SEQ, BATCH_STEP_EXECUTION_SEQ).

## On Spring Boot 4.0

Both tests fail:
```
AssertionError: BATCH_JOB_SEQ table should exist in Spring Batch 5.x schema.
In Spring Batch 6.0, it is renamed to BATCH_JOB_INSTANCE_SEQ.
```

Spring Batch 6.0 auto-initializes with new table names. If the application expects the old names, it fails at runtime with "table not found" errors.

## Impact Scenarios

**Scenario 1: Fresh H2 In-Memory Database**
- Spring Boot 3.x: Schema auto-initializes with old names
- Spring Boot 4.0: Schema auto-initializes with new names
- Result: No problem; both versions work independently

**Scenario 2: Pre-Initialized Production Schema**
- Existing database: Schema created with Spring Batch 5.x (old names)
- Spring Boot 3.x app: Works fine
- Spring Boot 4.0 app: Fails — "Table BATCH_JOB_SEQ not found" (Batch 6.0 expects BATCH_JOB_INSTANCE_SEQ)

**Scenario 3: Database Migration Tools (Flyway/Liquibase)**
- Migration scripts hard-code old table names
- Spring Boot 4.0 expects new names
- Schema mismatch; batch jobs fail at runtime

## Fix / Migration Path

**Option 1: Clean database (simplest, loses batch history)**

Let Spring Boot 4.0 auto-initialize the new schema:
```properties
spring.batch.jdbc.initialize-schema=always
```

Existing batch job history is lost. Best for development/test environments.

**Option 2: Preserve history with data migration**

For production databases with job run history:

1. Export data from old tables:
```sql
SELECT * FROM BATCH_JOB_SEQ;
SELECT * FROM BATCH_STEP_EXECUTION_SEQ;
```

2. Let Spring Boot 4.0 auto-create new schema (initialize-schema=always)

3. Migrate data to new tables

4. Verify all batch jobs reference correct tables

5. Drop old tables once verified

**Option 3: Database view for compatibility (during transition)**

Create views to support both old and new names temporarily:
```sql
CREATE VIEW BATCH_JOB_SEQ AS SELECT * FROM BATCH_JOB_INSTANCE_SEQ;
CREATE VIEW BATCH_STEP_EXECUTION_SEQ AS SELECT * FROM BATCH_STEP_EXECUTION_INSTANCE_SEQ;
```

Allows mixed-version deployments during gradual migration.

**Migration checklist:**

- [ ] Identify all Batch schema instances (dev, test, prod)
- [ ] Back up production Batch schema
- [ ] Audit database migration scripts for old table names
- [ ] Choose migration strategy (clean vs. preserve history)
- [ ] Test schema migration on non-prod environment
- [ ] Update migration scripts to use new table names
- [ ] Deploy Spring Boot 4.0 with updated schema
- [ ] Monitor for schema-related errors in production

## References

- Spring Batch 6.0 Migration Guide: https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide
- Spring Batch 6.0 Release Notes: https://github.com/spring-projects/spring-batch/releases
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
