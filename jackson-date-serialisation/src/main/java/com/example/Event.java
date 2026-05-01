package com.example;

/**
 * Placeholder — the actual date serialisation test uses java.util.Map (no model class)
 * and reflection to load Jackson's ObjectMapper from whichever package is available.
 *
 * The pre-migration pattern this module demonstrates:
 *
 *   // Jackson 2.x: {"occurredAt":1699257000000}       (numeric millis)
 *   // Jackson 3.x: {"occurredAt":"2023-11-06T05:30Z"} (ISO-8601 string)
 *   mapper.writeValueAsString(event);
 */
public class Event {
    // Intentionally empty — see test class for the actual demonstration
}
