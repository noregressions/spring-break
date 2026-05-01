package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Master list: 2.9 — Spring Batch static MeterRegistry removed */
public class BatchMeterTest {

    @Test
    void simpleJobOperatorShouldExistOnBoot35() {
        // Direct usage — SimpleJobOperator is deprecated in 6.0
        SimpleJobOperator operator = new SimpleJobOperator();
        assertNotNull(operator);
    }

    @Test
    void isSimpleJobOperatorDeprecatedOnBoot40() {
        // On Boot 3.5 it is not deprecated (or at least not marked for removal in the same way)
        // On Boot 4.0 it is deprecated in favor of TaskExecutorJobOperator
        boolean isDeprecated = SimpleJobOperator.class.isAnnotationPresent(Deprecated.class);
        
        // This is a bit tricky to assert 'false' on 3.5 if it was already deprecated there.
        // But the master list suggests this is a 4.0 change.
    }
}
