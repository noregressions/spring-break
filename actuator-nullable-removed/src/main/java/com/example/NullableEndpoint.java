package com.example;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "nullable")
public class NullableEndpoint {

    // On Boot 3.5: @org.springframework.lang.Nullable marks this param as optional.
    // Actuator's parameter binding respects it — calling without 'name' works fine.
    // On Boot 4.0: Spring Framework 7.0 / Boot 4.0 switched to JSpecify.
    // org.springframework.lang.Nullable is no longer recognised by Actuator binding.
    // The parameter is treated as required — missing it throws MissingServletRequestParameterException.
    @ReadOperation
    public String get(@Nullable String name) {
        return "Hello " + (name != null ? name : "World");
    }
}