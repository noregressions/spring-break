package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Service that uses the commons-logging API via the spring-jcl bridge.
 *
 * Spring Boot 3.5: spring-jcl is part of Spring Framework, managed by the Boot BOM, resolves fine.
 * Spring Boot 4.0: spring-jcl is removed from Spring Framework 7. The explicit declaration in
 *                  pom.xml fails to resolve at the dependency-resolution phase.
 *
 * Fix: Remove the explicit spring-jcl dependency. Commons Logging (1.3.x, managed by the Boot BOM)
 *      is now pulled in transitively via spring-core, so commons-logging APIs still work.
 *
 * Scope of this test: PROVES the failure mode (declaring spring-jcl breaks 4.0 builds).
 *                     DOES NOT exercise the broader cleanup advice in the cheat sheet
 *                     (removing jcl-over-slf4j, removing commons-logging exclusions). Those
 *                     are inferred recommendations, not failures demonstrated by this module.
 */
public class LoggingService {

    private static final Log log = LogFactory.getLog(LoggingService.class);

    public String doWork(String input) {
        log.info("Processing: " + input);
        return "processed-" + input;
    }
}
