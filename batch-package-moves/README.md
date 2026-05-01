# Spring Batch Core Package Moves (Tier 1: Won't Compile)

**Summary**: In Spring Batch 6.0, several core domain classes like `Job`, `JobExecution`, `JobInstance`, and `JobParameters` have been relocated from `org.springframework.batch.core` to `org.springframework.batch.core.job`.

## What breaks

In Spring Batch 5.x (Spring Boot 3.5), core domain classes were located directly in the `org.springframework.batch.core` package.

In Spring Batch 6.0 (Spring Boot 4.0), these classes have been moved to subpackages (mostly `org.springframework.batch.core.job`) as part of a domain model redesign. Code using the old imports will fail to compile.

```java
// Spring Boot 3.5 / Spring Batch 5.x (Works)
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;

// Spring Boot 4.0 / Spring Batch 6.0 (Compilation Error)
// package org.springframework.batch.core does not contain class Job
```

## How this test works

The module `batch-package-moves` contains:
- `BatchCoreUsage.java`: A class that imports core Batch classes from their 5.x packages.
- `BatchPackageTest.java`: A test verifying the classes exist in the old packages.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile because the classes are no longer in the imported packages.

## Fix / Migration Path

Update imports to the new packages.

```java
// Spring Boot 4.0 (Fixed)
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.JobParameters;
```

## References

- [Spring Batch 6.0 Migration Guide](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide) — Batch domain model changes
- Master list entry: 1.67
