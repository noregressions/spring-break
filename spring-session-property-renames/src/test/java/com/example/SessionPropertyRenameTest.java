package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the silent rename of the Spring Session Redis property prefix between Boot 3.5 and 4.0.
 *
 * The test reflects on the @ConfigurationProperties annotation of Boot's own
 * session-Redis properties class. Both the class name AND the package changed
 * between versions, so we look up by FQN per version and assert on the prefix:
 *
 *   Boot 3.5.14:
 *     org.springframework.boot.autoconfigure.session.RedisSessionProperties
 *     @ConfigurationProperties(prefix = "spring.session.redis")
 *
 *   Boot 4.0.6:
 *     org.springframework.boot.session.data.redis.autoconfigure.SessionDataRedisProperties
 *     @ConfigurationProperties(prefix = "spring.session.data.redis")
 *
 * The classpath-only check sidesteps the auto-config-firing complexity (Spring
 * Session's auto-config has a number of conditional triggers that aren't
 * straightforward to satisfy in a unit test without a live Redis). The rename
 * is what the user-impact card documents: code or config still using
 * `spring.session.redis.*` is silently ignored on Boot 4.0 because the new
 * properties class binds to `spring.session.data.redis.*`.
 *
 * On Boot 3.5: the Boot-3 class loads, prefix is "spring.session.redis" — passes.
 * On Boot 4.0: the Boot-3 class is gone; the Boot-4 class loads with prefix
 *              "spring.session.data.redis" — the asserted equality on the OLD
 *              prefix fails, demonstrating the rename.
 *
 * Fix: rename `spring.session.redis.` → `spring.session.data.redis.` (and
 *      `spring.session.mongodb.` → `spring.session.data.mongodb.` for the
 *      MongoDB equivalent) in every property file and external config source.
 */
class SessionPropertyRenameTest {

    private static final String BOOT_3_PROPS_CLASS =
        "org.springframework.boot.autoconfigure.session.RedisSessionProperties";
    private static final String BOOT_4_PROPS_CLASS =
        "org.springframework.boot.session.data.redis.autoconfigure.SessionDataRedisProperties";

    @Test
    void boot3PropertiesClassExistsAndBindsToLegacyPrefix() throws Exception {
        Class<?> clazz = loadEither(BOOT_3_PROPS_CLASS, BOOT_4_PROPS_CLASS);
        assertNotNull(clazz,
            "Either Boot 3's RedisSessionProperties or Boot 4's SessionDataRedisProperties " +
            "should be on the classpath. Neither was found — check your dependencies.");

        ConfigurationProperties ann = clazz.getAnnotation(ConfigurationProperties.class);
        assertNotNull(ann,
            clazz.getName() + " should be annotated with @ConfigurationProperties");

        String prefix = !ann.prefix().isEmpty() ? ann.prefix() : ann.value();

        assertEquals(
            "spring.session.redis",
            prefix,
            "On Boot 3.x the canonical Spring Session Redis property prefix is " +
            "'spring.session.redis'. On Boot 4.0 the prefix has been silently renamed to " +
            "'spring.session.data.redis' — any application.properties using the old " +
            "prefix is silently ignored. Found prefix: '" + prefix + "' on class " + clazz.getName()
        );
    }

    private static Class<?> loadEither(String... fqcns) {
        for (String fqcn : fqcns) {
            try { return Class.forName(fqcn); } catch (ClassNotFoundException ignored) {}
        }
        return null;
    }
}
