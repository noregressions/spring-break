package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;

/**
 * Demonstrates Jackson 2.x API using com.fasterxml.jackson group ID.
 *
 * COMPILES on Spring Boot 3.5 (Jackson 2.x) but FAILS on Spring Boot 4.0 (Jackson 3.0).
 *
 * Breaking changes:
 * 1. Group ID: com.fasterxml.jackson → tools.jackson (Maven dependency)
 * 2. Classes renamed: JsonSerializer → ValueSerializer, etc.
 * 3. Packages: com.fasterxml.jackson.* → tools.jackson.*
 *
 * This is a Tier 1 failure: compile-time error. Build breaks immediately.
 *
 * References:
 * - Jackson 3.0 Changes: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
 * - Spring Boot 4.0 Migration: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
 */
public class JacksonGroupIdDemo {

    /**
     * Custom serializer using Jackson 2.x API (extends JsonSerializer).
     *
     * On Boot 4.0/Jackson 3.0: This will fail to compile because:
     * - JsonSerializer class moved from com.fasterxml.jackson.databind to tools.jackson.databind
     * - JsonSerializer was renamed to ValueSerializer
     * - SerializerProvider was renamed to SerializationContext
     *
     * Migration: extend ValueSerializer instead and use tools.jackson imports.
     * See: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
     */
    public static class NamePrefixSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString("[CUSTOM] " + value);
        }
    }

    public static class User {
        @JsonSerialize(using = NamePrefixSerializer.class)
        private String name;
        private int age;

        public User() {}

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public void setName(String name) { this.name = name; }
        public void setAge(int age) { this.age = age; }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("Alice", 30);
        String json = objectMapper.writeValueAsString(user);
        System.out.println("Serialized: " + json);
    }
}
