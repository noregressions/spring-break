package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the silent rename of MongoDB configuration properties between Boot 3.5 and 4.0.
 *
 * Boot 3.5.14:
 * - MongoProperties lives at org.springframework.boot.autoconfigure.mongo.MongoProperties
 *   (in spring-boot-autoconfigure) and binds to spring.data.mongodb.*.
 * - With spring.data.mongodb.uri=mongodb://nonexistent-host.test:... in application.properties:
 *     • MongoProperties.getUri() returns the configured URI string.
 * - assertion below passes.
 *
 * Boot 4.0.6:
 * - MongoProperties moved to org.springframework.boot.mongodb.autoconfigure.MongoProperties
 *   (in spring-boot-mongodb, the new per-concern jar) and binds to spring.mongodb.*.
 * - The legacy spring.data.mongodb.* prefix is silently ignored.
 * - With only the legacy property set:
 *     • MongoProperties.getUri() returns null (or the Boot default).
 *     • The application connects to localhost:27017 with no auth.
 * - assertion below fails — the test catches the regression.
 *
 * The test uses reflection to look up MongoProperties because the type's package
 * differs between Boot 3.5 (...autoconfigure.mongo.MongoProperties) and Boot 4.0
 * (...mongodb.autoconfigure.MongoProperties). The simple class name and getUri()
 * method are unchanged across versions, so reflection works on both.
 *
 * Fix: rename the property prefix in every property file, env var, and external
 * config source — `spring.data.mongodb.` → `spring.mongodb.`.
 */
@SpringBootTest
class MongoPropertyRenameTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void legacyPropertyShouldBeRecognised() throws Exception {
        // Find Boot's MongoProperties bean by simple class name (cross-version safe).
        Object mongoProps = null;
        for (String name : context.getBeanDefinitionNames()) {
            Object bean = context.getBean(name);
            if (bean != null && "MongoProperties".equals(bean.getClass().getSimpleName())) {
                mongoProps = bean;
                break;
            }
        }
        assertNotNull(mongoProps,
            "MongoProperties bean should be present (auto-configured by spring-boot-starter-data-mongodb)");

        Method getUri = mongoProps.getClass().getMethod("getUri");
        Object uri = getUri.invoke(mongoProps);

        assertTrue(
            uri != null && uri.toString().contains("nonexistent-host.test"),
            "MongoProperties.getUri() should reflect the configured spring.data.mongodb.uri " +
            "value on Boot 3.x. On Boot 4.0 the property is silently ignored — rename to " +
            "spring.mongodb.uri. Got: " + uri
        );
    }
}
