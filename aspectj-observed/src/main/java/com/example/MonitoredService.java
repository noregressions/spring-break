package com.example;

import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

/**
 * A service with an @Observed method.
 *
 * Spring Boot 3.5: auto-config handles the ObservedAspect, observations are recorded.
 * Spring Boot 4.0: without explicit AspectJ weaver, observations are silently not recorded.
 */
@Service
public class MonitoredService {

    @Observed(name = "monitored.operation")
    public String performOperation() {
        return "operation-complete";
    }
}
