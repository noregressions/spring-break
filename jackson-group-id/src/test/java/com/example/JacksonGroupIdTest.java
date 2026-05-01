package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Jackson group ID breaking change between Boot 3.5 and 4.0.
 *
 * Spring Boot 3.5 (Jackson 2.x):
 * - Dependency: com.fasterxml.jackson.core:jackson-databind
 * - Classes available at: com.fasterxml.jackson.databind.*
 * - Result: Tests compile and pass ✓
 *
 * Spring Boot 4.0 (Jackson 3.0):
 * - Dependency: tools.jackson.core:jackson-databind
 * - Classes moved to: tools.jackson.databind.*
 * - Result: Compilation fails ✗
 *   error: package com.fasterxml.jackson.databind does not exist
 *
 * This is a Tier 1 failure: compile-time error preventing build.
 *
 * Fix: Replace all Jackson imports with tools.jackson.* paths.
 * References:
 * - Jackson 3.0 Release Notes: https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0
 * - Spring Boot Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
class JacksonGroupIdTest {

    @Test
    void objectMapperUsesOldGroupId() throws JsonProcessingException {
        // On Boot 3.5: import com.fasterxml.jackson.databind.ObjectMapper resolves
        // because spring-boot-starter-json includes com.fasterxml.jackson.core:jackson-databind.
        //
        // On Boot 4.0: This import path fails because ObjectMapper moved to
        // tools.jackson.databind.ObjectMapper (via tools.jackson.core:jackson-databind).
        //
        // Fix: Update the import to:
        //   import tools.jackson.databind.ObjectMapper;
        // Then update pom.xml to use tools.jackson group ID.
        ObjectMapper mapper = new ObjectMapper();
        assertNotNull(mapper);

        String json = mapper.writeValueAsString(
                java.util.Map.of("framework", "Spring Boot", "version", "3.5")
        );
        assertTrue(json.contains("Spring Boot"));
    }

    @Test
    void customSerializerUsesOldGroupId() throws JsonProcessingException {
        // This test verifies custom serializers work in Boot 3.5 using Jackson 2.x APIs.
        // The NamePrefixSerializer class extends JsonSerializer<String>, which is only
        // available at com.fasterxml.jackson.databind.JsonSerializer on Boot 3.x.
        //
        // On Boot 4.0 (Jackson 3.0), this breaks because:
        // 1. JsonSerializer moved to tools.jackson.databind package
        // 2. JsonSerializer was renamed to ValueSerializer
        // 3. SerializerProvider was renamed to SerializationContext
        //
        // Fix: Update NamePrefixSerializer to extend ValueSerializer and use tools.jackson imports.
        // See: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
        ObjectMapper mapper = new ObjectMapper();
        JacksonGroupIdDemo.User user = new JacksonGroupIdDemo.User("Alice", 30);

        String json = mapper.writeValueAsString(user);
        assertNotNull(json);
        // Custom serializer wraps name with [CUSTOM] prefix
        assertTrue(json.contains("[CUSTOM]"), "Custom serializer should apply. JSON: " + json);
    }
}
