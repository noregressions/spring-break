package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/* Master list: 1.44 — StreamsBuilderFactoryBeanCustomizer removed. */
class KafkaCustomizerTest {

    @Test
    void customizerCanBeInstantiated() {
        KafkaCustomizerUsage usage = new KafkaCustomizerUsage();
        assertNotNull(usage);
    }

    @Test
    void customizerIsLoadableViaReflection() {
        assertDoesNotThrow(
            () -> Class.forName("org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer"),
            "StreamsBuilderFactoryBeanCustomizer should be available on Boot 3.5"
        );
    }
}
