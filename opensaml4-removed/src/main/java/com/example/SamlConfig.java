package com.example;

import org.springframework.context.annotation.Configuration;

/**
 * References OpenSaml4AuthenticationProvider by class name.
 * In Boot 3.5, this class exists in Spring Security.
 * In Boot 4.0, OpenSAML 4 support is removed — only OpenSAML 5 is supported.
 */
@Configuration
public class SamlConfig {

    public String getSamlProviderClassName() {
        // In Boot 3.5, this class exists. In Boot 4.0, it's removed.
        return "org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider";
    }
}
