# Spring Batch Static MeterRegistry Removed (Tier 2: Won't Run)

**Summary**: In Spring Batch 6.0, the internal usage of Micrometer's global static `MeterRegistry` has been removed. Additionally, `SimpleJobOperator` has been deprecated in favor of `TaskExecutorJobOperator`.

## What breaks

In Spring Batch 5.x (Spring Boot 3.5), some internal Batch components (like those used by `SimpleJobOperator`) might have implicitly relied on or allowed the use of Micrometer's static global registry for metrics collection.

In Spring Batch 6.0 (Spring Boot 4.0), all metrics collection has been moved to use the `ObservationRegistry` and explicitly provided `MeterRegistry` beans. The static registry is no longer used. Furthermore, `SimpleJobOperator` is deprecated.

Applications that relied on automatic metrics collection via the static registry without providing an explicit `MeterRegistry` or `ObservationRegistry` to Batch infrastructure may find that Batch metrics are no longer being recorded.

## How this test works

The module `batch-static-meterregistry-removed` contains:
- `BatchMeterUsage.java`: A class that references `SimpleJobOperator`.
- `BatchMeterTest.java`: A test verifying the existence of the operator.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Demonstrates the deprecation and shift in infrastructure.

## Fix / Migration Path

1. Migrate from `SimpleJobOperator` to `TaskExecutorJobOperator` (or use `JobOperatorFactoryBean` which now creates a `TaskExecutorJobOperator`).
2. Ensure an `ObservationRegistry` and/or `MeterRegistry` bean is provided and correctly injected into the Batch infrastructure.

```java
// Spring Boot 4.0 (Recommended Fix)
@Bean
public JobOperator jobOperator(JobRepository jobRepository, 
                               JobExplorer jobExplorer, 
                               JobRegistry jobRegistry, 
                               JobLauncher jobLauncher) {
    TaskExecutorJobOperator operator = new TaskExecutorJobOperator();
    operator.setJobRepository(jobRepository);
    operator.setJobExplorer(jobExplorer);
    operator.setJobRegistry(jobRegistry);
    operator.setJobLauncher(jobLauncher);
    // Metrics are now handled via ObservationRegistry
    return operator;
}
```

## References

- [Spring Batch 6.0 Migration Guide](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide)
- Master list entry: 2.9
