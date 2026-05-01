package com.example;

import org.springframework.batch.core.launch.support.SimpleJobOperator;

public class BatchMeterUsage {
    public static void configure(SimpleJobOperator operator) {
        // SimpleJobOperator is deprecated in Spring Batch 6.0
        // and its internal usage of static MeterRegistry has been removed.
    }
}
