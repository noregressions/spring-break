package com.example;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Uses the type-level @ConstructorBinding annotation from the old package.
 *
 * Spring Boot 3.5: org.springframework.boot.context.properties.ConstructorBinding
 *                   is deprecated but still present — compiles with a warning.
 * Spring Boot 4.0: that class is deleted.  The only surviving @ConstructorBinding
 *                   lives in o.s.b.context.properties.bind.ConstructorBinding and
 *                   can only be placed on a constructor, not on a type.
 *                   → import fails → compilation error.
 */
@SuppressWarnings("deprecation")
@ConfigurationProperties(prefix = "app")
@org.springframework.boot.context.properties.ConstructorBinding
public class AppConfig {

    private final String host;
    private final int port;
    private final boolean debug;

    public AppConfig(String host, int port, boolean debug) {
        this.host = host;
        this.port = port;
        this.debug = debug;
    }

    public String getHost()  { return host; }
    public int    getPort()  { return port; }
    public boolean isDebug() { return debug; }
}
