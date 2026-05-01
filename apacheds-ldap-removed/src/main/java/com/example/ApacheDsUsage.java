package com.example;

import org.springframework.security.ldap.server.ApacheDSContainer;

public class ApacheDsUsage {
    public static boolean isContainerPresent() {
        try {
            Class.forName("org.springframework.security.ldap.server.ApacheDSContainer", false, ApacheDsUsage.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }
}
