package com.example;

import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

public class KafkaCustomizerUsage implements StreamsBuilderFactoryBeanCustomizer {
    @Override
    public void customize(StreamsBuilderFactoryBean factoryBean) {
        // customization logic
    }
}
