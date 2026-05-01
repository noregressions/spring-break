package com.example.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

/**
 * On Boot 3.5 (Spring Batch 5.x):
 *   JobExecutionListenerSupport exists — compiles fine.
 *
 * On Boot 4.0 (Spring Batch 6.0):
 *   JobExecutionListenerSupport is deleted — compile fails:
 *   "cannot find symbol: class JobExecutionListenerSupport"
 *
 * Fix: replace "extends JobExecutionListenerSupport"
 *      with "implements JobExecutionListener"
 *      (interfaces have had default methods since Batch 5.0)
 */
public class JobCompletionListener extends JobExecutionListenerSupport {

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Job finished with status: " + jobExecution.getStatus());
    }
}
