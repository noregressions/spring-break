package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Service that uses commons-logging via the spring-jcl bridge.
 *
 * Spring Boot 3.5: spring-jcl provides the commons-logging bridge, resolves fine.
 * Spring Boot 4.0: spring-jcl removed; Spring uses commons-logging 1.3.0 directly.
 *                   The explicit spring-jcl dependency in pom.xml fails to resolve.
 *
 * Fix: Remove the explicit spring-jcl dependency. Commons-logging APIs still work
 *      via the commons-logging 1.3.0 artifact that Spring Framework 7 depends on directly.
 */
public class LoggingService {

    private static final Log log = LogFactory.getLog(LoggingService.class);

    public String doWork(String input) {
        log.info("Processing: " + input);
        return "processed-" + input;
    }
}
