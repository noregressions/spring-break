# Spring Batch ChunkHandler Renamed (Tier 1: Won't Compile)

**Summary**: In Spring Batch 6.0, `ChunkHandler` has been renamed to `ChunkRequestHandler`. Additionally, `JobStep.setJobLauncher` has been removed in favor of `setJobOperator`.

## What breaks

As part of the infrastructure overhaul in Spring Batch 6.0 (Spring Boot 4.0), several components have been renamed or their dependencies shifted:

1. `org.springframework.batch.integration.chunk.ChunkHandler` is now `org.springframework.batch.integration.chunk.ChunkRequestHandler`.
2. `JobStep` (and its builder) no longer accepts a `JobLauncher`. It now requires a `JobOperator`.

Code using the old class name or method will fail to compile.

```java
// Spring Boot 3.5 / Spring Batch 5.x (Works)
import org.springframework.batch.integration.chunk.ChunkHandler;
// ...
jobStep.setJobLauncher(launcher);

// Spring Boot 4.0 / Spring Batch 6.0 (Compilation Error)
// cannot find symbol: class ChunkHandler
// cannot find symbol: method setJobLauncher(org.springframework.batch.core.launch.JobLauncher)
```

## How this test works

The module `batch-chunkhandler-renamed` contains:
- `BatchRenamingUsage.java`: A class that uses `ChunkHandler` and calls `JobStep.setJobLauncher`.
- `BatchRenamingTest.java`: A test verifying these exist on 3.5.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile due to the renaming and method removal.

## Fix / Migration Path

1. Rename `ChunkHandler` to `ChunkRequestHandler`.
2. Replace `setJobLauncher(JobLauncher)` with `setJobOperator(JobOperator)`.

```java
// Spring Boot 4.0 (Fixed)
import org.springframework.batch.integration.chunk.ChunkRequestHandler;
// ...
jobStep.setJobOperator(operator);
```

## References

- [Spring Batch 6.0 Migration Guide](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide)
- Master list entry: 1.69
