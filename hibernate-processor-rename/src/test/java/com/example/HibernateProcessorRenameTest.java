package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * On Spring Boot 3.5: hibernate-jpamodelgen exists, Product_ metamodel class is generated.
 * On Spring Boot 4.0: hibernate-jpamodelgen artifact is gone (renamed to hibernate-processor),
 *                      dependency resolution fails — build won't complete.
 */
class HibernateProcessorRenameTest {

    @Test
    void metamodelClassShouldBeGenerated() throws ClassNotFoundException {
        Class<?> metamodel = Class.forName("com.example.Product_");
        assertNotNull(metamodel, "Product_ metamodel class should have been generated");
    }
}
