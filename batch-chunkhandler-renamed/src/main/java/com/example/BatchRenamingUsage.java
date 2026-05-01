package com.example;

import org.springframework.batch.integration.chunk.ChunkHandler;
import org.springframework.batch.core.step.job.JobStep;
import org.springframework.batch.core.launch.JobLauncher;

public class BatchRenamingUsage {
    public static void useChunkHandler(ChunkHandler<?> handler) {
        // ChunkHandler is renamed to ChunkRequestHandler in Spring Batch 6.0
    }

    public static void configureJobStep(JobStep jobStep, JobLauncher launcher) {
        // JobStep.setJobLauncher(JobLauncher) is removed in favor of setJobOperator(JobOperator)
        jobStep.setJobLauncher(launcher);
    }
}
