# Spring Batch JobBuilder(String) Constructor Removed (Tier 1: Won't Compile)

**Summary**: The `JobBuilder(String)` constructor has been removed in Spring Batch 6.0. All job and step builders now require a `JobRepository` to be provided at construction time.

## What breaks

In Spring Batch 5.x (Spring Boot 3.5), `JobBuilder` could be instantiated with just a name. The `JobRepository` could be set later via the `.repository()` method.

In Spring Batch 6.0 (Spring Boot 4.0), this constructor is removed. Code that uses it will fail to compile.

```java
// Spring Boot 3.5 / Spring Batch 5.x (Works)
JobBuilder builder = new JobBuilder("myJob");

// Spring Boot 4.0 / Spring Batch 6.0 (Compilation Error)
// cannot find symbol: constructor JobBuilder(java.lang.String)
```

## How this test works

The module `batch-job-builder-string-constructor` contains:
- `JobBuilderUsage.java`: A class that calls `new JobBuilder(name)`.
- `JobBuilderTest.java`: A test verifying the constructor exists.

On Boot 3.5: Compiles and passes.
On Boot 4.0: Fails to compile with a "cannot find symbol" error for the constructor.

## Fix / Migration Path

Inject the `JobRepository` and pass it to the constructor.

```java
// Spring Boot 4.0 (Fixed)
@Bean
public Job myJob(JobRepository jobRepository, Step step) {
    return new JobBuilder("myJob", jobRepository)
            .start(step)
            .build();
}
```

## References

- [Spring Batch 6.0 Migration Guide](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-6.0-Migration-Guide)
- Master list entry: 1.68
