package com.example;

import org.springframework.batch.core.job.builder.JobBuilder;

public class JobBuilderUsage {
    public static JobBuilder createBuilder(String name) {
        // This constructor JobBuilder(String) is removed in Spring Batch 6.0
        // Use JobBuilder(String, JobRepository) instead
        return new JobBuilder(name);
    }
}
