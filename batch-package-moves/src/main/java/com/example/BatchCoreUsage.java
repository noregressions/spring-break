package com.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

public class BatchCoreUsage {
    public static void process(Job job, JobExecution execution) {
        JobInstance instance = execution.getJobInstance();
        JobParameters params = execution.getJobParameters();
    }
}
