package com.example;

import org.springframework.boot.jackson.JsonComponent;

/**
 * Demonstrates the @JsonComponent → @JacksonComponent rename in Boot 4.0.
 *
 * Spring Boot 3.5 (3.5.14):
 * - org.springframework.boot.jackson.JsonComponent is in the spring-boot core jar.
 * - This class compiles. The annotation has no runtime effect here — there's no
 *   Jackson serializer to register — but that's fine; we're proving the import
 *   resolves and javac accepts the annotation.
 *
 * Spring Boot 4.0 (4.0.6):
 * - The annotation was renamed to @JacksonComponent and moved to the new
 *   per-concern spring-boot-jackson jar (still at org.springframework.boot.jackson.*).
 * - The Boot-3 import does not resolve. javac fails:
 *     "package org.springframework.boot.jackson does not exist"
 *     or
 *     "cannot find symbol: class JsonComponent"
 *
 * Migration: rename the import (JsonComponent → JacksonComponent) and update
 * the annotation usage. Add spring-boot-jackson explicitly if the per-concern
 * jar isn't pulled in transitively in your build (the spring-boot-starter-jackson
 * starter brings it in for typical web apps).
 *
 * Note: this module deliberately does NOT extend a Jackson serializer class.
 * The card's no_module_reason claimed the rename can't be tested in isolation
 * because Jackson 3 moved JsonSerializer to a different package — that's true
 * for any class that BOTH uses @JsonComponent AND extends JsonSerializer. By
 * decoupling the two, we isolate the annotation-rename failure cleanly.
 */
@JsonComponent
public class CustomSerializerMarker {
    // No serializer body — the @JsonComponent annotation alone is the load-bearing piece.
}
