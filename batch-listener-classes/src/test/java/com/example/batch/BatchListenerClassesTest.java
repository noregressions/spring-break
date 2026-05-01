package com.example.batch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Category (a) — Won't Compile on Boot 4.0
 *
 * This test exists only to make Maven run the compile phase against the
 * source files above. The real failure is at "mvn compile" on Boot 4.0:
 *
 *   [ERROR] JobCompletionListener.java: cannot find symbol
 *     class JobExecutionListenerSupport
 *   [ERROR] StepLoggingListener.java: cannot find symbol
 *     class StepExecutionListenerSupport
 *
 * On Boot 3.5 both classes compile and this test passes.
 */
class BatchListenerClassesTest {

    @Test
    void jobListenerCanBeInstantiated() {
        JobCompletionListener listener = new JobCompletionListener();
        assertNotNull(listener);
    }

    @Test
    void stepListenerCanBeInstantiated() {
        StepLoggingListener listener = new StepLoggingListener();
        assertNotNull(listener);
    }
}
