package com.example;

/**
 * Placeholder — the actual exception hierarchy test is in JsonParserServiceTest.java
 * and uses reflection to load Jackson classes from whichever package is available.
 *
 * The pre-migration pattern this module demonstrates:
 *
 *   try {
 *       Object parsed = objectMapper.readValue(jsonString, Object.class);
 *   } catch (IOException e) {
 *       // This catch block stops working on Jackson 3.x because
 *       // JacksonException is now a RuntimeException, not an IOException
 *   }
 */
public class JsonParserService {
    // Intentionally empty — see test class for the actual demonstration
}
