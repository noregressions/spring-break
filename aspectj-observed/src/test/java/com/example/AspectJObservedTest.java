package com.example;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Verifies that @Observed methods produce observations.
 *
 * Spring Boot 3.5: ObservedAspect auto-config works, observation is recorded — passes.
 * Spring Boot 4.0: Without AspectJ weaver, observation silently not recorded — fails.
 */
@SpringBootTest
class AspectJObservedTest {

    @TestConfiguration
    static class TestObservationConfig {
        @Bean
        @Primary
        public ObservationRegistry testObservationRegistry() {
            return TestObservationRegistry.create();
        }
    }

    @Autowired
    private MonitoredService monitoredService;

    @Autowired
    private ObservationRegistry observationRegistry;

    @Test
    void observedAnnotationShouldRecordObservation() {
        // Call the @Observed method
        monitoredService.performOperation();

        // Verify the observation was recorded
        TestObservationRegistryAssert.assertThat((TestObservationRegistry) observationRegistry)
            .hasObservationWithNameEqualTo("monitored.operation")
            .that()
            .hasBeenStarted()
            .hasBeenStopped();
    }
}
