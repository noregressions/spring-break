package com.example.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

/**
 * On Boot 3.5 (Spring Batch 5.x):
 *   StepExecutionListenerSupport exists — compiles fine.
 *
 * On Boot 4.0 (Spring Batch 6.0):
 *   StepExecutionListenerSupport is deleted — compile fails:
 *   "cannot find symbol: class StepExecutionListenerSupport"
 *
 * Fix: replace "extends StepExecutionListenerSupport"
 *      with "implements StepExecutionListener"
 */
public class StepLoggingListener extends StepExecutionListenerSupport {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("Step finished: " + stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }
}
