package com.example;

/**
 * Placeholder — the actual locale serialisation test uses java.util.Map (no model class)
 * and reflection to load Jackson's ObjectMapper from whichever package is available.
 *
 * The pre-migration pattern this module demonstrates:
 *
 *   // Jackson 2.x: {"locale":"zh_CN"}   (Java toString format)
 *   // Jackson 3.x: {"locale":"zh-CN"}   (IETF BCP 47 format)
 *   mapper.writeValueAsString(prefs);
 */
public class UserPreferences {
    // Intentionally empty — see test class for the actual demonstration
}
