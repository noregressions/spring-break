package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Master list: 2.2 — Health probes default on */
@SpringBootTest
public class HealthProbesTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void probesShouldBeDisabledByDefaultOnBoot35() {
        // In Boot 3.5, liveness/readiness probes are only enabled if running on Kubernetes
        // or if management.endpoint.health.probes.enabled is true.
        
        boolean hasLiveness = context.containsBean("livenessStateHealthIndicator");
        boolean hasReadiness = context.containsBean("readinessStateHealthIndicator");
        
        assertFalse(hasLiveness, 
            "Liveness probe group should NOT be present by default on Boot 3.5. " +
            "In Boot 4.0, it is enabled by default.");
        assertFalse(hasReadiness, 
            "Readiness probe group should NOT be present by default on Boot 3.5. " +
            "In Boot 4.0, it is enabled by default.");
    }
}
