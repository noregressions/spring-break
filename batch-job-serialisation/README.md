# Spring Batch 6.0 Builder Factory Removal

JobBuilderFactory and StepBuilderFactory removed; use JobBuilder and StepBuilder with direct constructor instantiation.

## What Breaks

Spring Batch 6.0 (Spring Boot 4.0) removes `JobBuilderFactory` and `StepBuilderFactory` which were deprecated in Spring Batch 5.0 but still available. These factory classes were a convenient way to autowire and build jobs/steps in configuration classes.

**Code that breaks:**
```java
@Autowired
private JobBuilderFactory jobBuilderFactory;  // ClassNotFoundException

@Autowired
private StepBuilderFactory stepBuilderFactory;  // ClassNotFoundException

@Bean
public Job myJob() {
    return jobBuilderFactory.get("myJob")  // Won't compile
        .start(step1())
        .build();
}
```

**Error on Spring Boot 4.0:**
```
ClassNotFoundException: org.springframework.batch.core.configuration.annotation.JobBuilderFactory
```

Code that simply imports these classes fails at compile time. Code that dynamically loads them fails at runtime.

**Classes removed:**
- `org.springframework.batch.core.configuration.annotation.JobBuilderFactory`
- `org.springframework.batch.core.configuration.annotation.StepBuilderFactory`

**Replacement classes:**
- `org.springframework.batch.core.job.builder.JobBuilder` (direct constructor, not a factory)
- `org.springframework.batch.core.step.builder.StepBuilder` (direct constructor, not a factory)

**Why it was removed:** The factory pattern was a convenience layer that hid dependency injection complexity. Spring Batch 6.0 favors explicit, direct instantiation where dependencies (JobRepository, PlatformTransactionManager) are passed explicitly to the builder constructors.

## How This Test Works

The test uses reflection to check for the existence of factory classes and their replacements:

- **jobBuilderFactoryShouldExist()**: Attempts to load JobBuilderFactory class. Passes on Batch 5.x, fails on 6.0 (class doesn't exist).
- **stepBuilderFactoryShouldExist()**: Attempts to load StepBuilderFactory class. Passes on Batch 5.x, fails on 6.0.
- **newJobBuilderShouldExist()**: Attempts to load JobBuilder class (direct constructor). Passes on both versions.
- **newStepBuilderShouldExist()**: Attempts to load StepBuilder class (direct constructor). Passes on both versions.

## On Spring Boot 3.5.14

```bash
mvn clean test
```

Output: All 4 tests pass. Both old factories and new builders are available.

## On Spring Boot 4.0

Factory-related tests fail:
```
jobBuilderFactoryShouldExist FAILS: ClassNotFoundException
stepBuilderFactoryShouldExist FAILS: ClassNotFoundException
```

New builder tests pass (JobBuilder and StepBuilder classes exist). If configuration code tries to autowire the old factories, compilation fails.

## Fix / Migration Path

**Before (Spring Batch 5.x):**
```java
@Configuration
@EnableBatchProcessing
public class BatchConfig {
  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job myJob(Step step1) {
    return jobBuilderFactory.get("myJob")
        .start(step1)
        .build();
  }

  @Bean
  public Step step1(ItemReader reader, ItemWriter writer) {
    return stepBuilderFactory.get("step1")
        .<String, String>chunk(10)
        .reader(reader)
        .writer(writer)
        .build();
  }
}
```

**After (Spring Batch 6.0):**
```java
@Configuration
@EnableBatchProcessing
public class BatchConfig {
  // Inject dependencies directly, no more factories
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  public BatchConfig(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager) {
    this.jobRepository = jobRepository;
    this.transactionManager = transactionManager;
  }

  @Bean
  public Job myJob(Step step1) {
    return new JobBuilder("myJob", jobRepository)
        .start(step1)
        .build();
  }

  @Bean
  public Step step1(ItemReader reader, ItemWriter writer) {
    return new StepBuilder("step1", jobRepository)
        .<String, String>chunk(10)
        .reader(reader)
        .writer(writer)
        .transactionManager(transactionManager)  // Must be explicit
        .build();
  }
}
```

**Key changes:**

| Old (Factory) | New (Direct) |
|---|---|
| `@Autowired JobBuilderFactory` | Inject `JobRepository` directly |
| `jobBuilderFactory.get("name")` | `new JobBuilder("name", jobRepository)` |
| `stepBuilderFactory.get("name")` | `new StepBuilder("name", jobRepository)` |
| Factory handles transactionManager | Must call `.transactionManager(tm)` explicitly |

**Migration checklist:**

- [ ] Remove all `@Autowired JobBuilderFactory` and `@Autowired StepBuilderFactory`
- [ ] Add constructor injection for `JobRepository` and `PlatformTransactionManager`
- [ ] Replace all `jobBuilderFactory.get(...)` with `new JobBuilder(...)`
- [ ] Replace all `stepBuilderFactory.get(...)` with `new StepBuilder(...)`
- [ ] Add `.transactionManager(transactionManager)` to all StepBuilder chains
- [ ] Run tests to verify job/step configuration builds and executes correctly
- [ ] Deploy with confidence

## References

- Spring Batch 6.0 Migration Guide: https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide
- Spring Batch 6.0 Release Notes: https://github.com/spring-projects/spring-batch/releases
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
