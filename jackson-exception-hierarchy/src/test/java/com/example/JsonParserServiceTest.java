package com.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the Jackson exception hierarchy breaking change between 2.x and 3.x.
 *
 * BREAKING CHANGE: JacksonException inheritance changed.
 *
 * Jackson 2.x (Spring Boot 3.5):
 *   JacksonException extends IOException (checked exception)
 *   → catch(IOException) handles all Jackson parse errors ✓
 *
 * Jackson 3.x (Spring Boot 4.0):
 *   JacksonException extends RuntimeException (unchecked)
 *   → catch(IOException) silently misses all Jackson parse errors ✗
 *
 * DANGER: This is the most silent breaking change because the code compiles
 * perfectly on both versions. The catch block remains syntactically valid Java
 * but stops catching what it used to. Applications may silently drop error
 * handling in production.
 *
 * MIGRATION: Use reflection to load JacksonException from either package so
 * this test compiles and runs on both Jackson 2 and Jackson 3.
 *
 * Reference: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
 */
class JsonParserServiceTest {

    /**
     * Locate the JacksonException class — it lives in different packages
     * depending on the Jackson version.
     */
    private Class<?> findJacksonExceptionClass() throws ClassNotFoundException {
        try {
            return Class.forName("com.fasterxml.jackson.core.JacksonException");
        } catch (ClassNotFoundException e) {
            return Class.forName("tools.jackson.core.JacksonException");
        }
    }

    @Test
    void jacksonExceptionExtendsIOException() throws Exception {
        Class<?> jacksonException = findJacksonExceptionClass();

        // BREAKING CHANGE TEST: Check if JacksonException is assignable from IOException.
        // This reflects the exception hierarchy change between Jackson 2 and 3.
        //
        // Jackson 2.x: JacksonException → StreamReadException → JsonProcessingException
        //   → JacksonException → IOException. This assertion PASSES.
        //
        // Jackson 3.x: JacksonException extends RuntimeException (not IOException).
        //   IOException.class.isAssignableFrom(JacksonException) → FALSE. Test FAILS.
        //
        // Reference: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
        assertTrue(
            IOException.class.isAssignableFrom(jacksonException),
            "JacksonException (" + jacksonException.getName() + ") no longer extends IOException. " +
            "Any catch(IOException) blocks that relied on catching Jackson parse errors " +
            "will silently stop working. The exception now propagates as a RuntimeException."
        );
    }

    @Test
    void catchIOExceptionPatternStillWorks() throws Exception {
        // BREAKING CHANGE TEST: Simulate the pre-migration catch(IOException) pattern.
        //
        // Common pre-4.0 code:
        //   try { objectMapper.readValue(...) } catch (IOException e) { handle(); }
        //
        // This test loads ObjectMapper via reflection, parses malformed JSON,
        // and verifies the exception IS caught by catch(IOException) blocks.
        //
        // On Jackson 2.x: Exception is IOException or subclass → test passes
        // On Jackson 3.x: Exception is RuntimeException → test fails (exception escapes)
        //
        // Reference: https://github.com/FasterXML/jackson/wiki/Jackson-3.0-Changes
        Object mapper = createObjectMapper();
        Method readValue = mapper.getClass().getMethod("readValue", String.class, Class.class);

        try {
            readValue.invoke(mapper, "{invalid json}", Object.class);
            fail("Expected a parse exception from malformed JSON");
        } catch (java.lang.reflect.InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            // On Jackson 2.x: cause is IOException (or subclass) → assertTrue passes
            // On Jackson 3.x: cause is RuntimeException → assertTrue fails
            assertTrue(
                cause instanceof IOException,
                "Jackson parse error is no longer an IOException. " +
                "Got: " + cause.getClass().getName() + " — this means catch(IOException) " +
                "blocks will miss Jackson errors. Actual exception: " + cause.getMessage()
            );
        }
    }

    @Test
    void validJsonStillParses() throws Exception {
        Object mapper = createObjectMapper();
        Method readValue = mapper.getClass().getMethod("readValue", String.class, Class.class);

        // Should work identically on both versions
        Object result = readValue.invoke(mapper, "{\"key\": \"value\"}", Object.class);
        assertNotNull(result, "Valid JSON should parse successfully on any Jackson version");
    }

    private Object createObjectMapper() throws Exception {
        Class<?> omClass;
        try {
            omClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        } catch (ClassNotFoundException e) {
            omClass = Class.forName("tools.jackson.databind.ObjectMapper");
        }
        return omClass.getDeclaredConstructor().newInstance();
    }
}
