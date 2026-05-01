package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.67 — Spring Batch package moves (Job, JobExecution, etc.) */
public class BatchPackageTest {

    @Test
    void coreClassesShouldExistInOldPackagesOnBoot35() {
        assertDoesNotThrow(() -> Class.forName("org.springframework.batch.core.Job"));
        assertDoesNotThrow(() -> Class.forName("org.springframework.batch.core.JobExecution"));
        assertDoesNotThrow(() -> Class.forName("org.springframework.batch.core.JobInstance"));
        assertDoesNotThrow(() -> Class.forName("org.springframework.batch.core.JobParameters"));
    }
}
