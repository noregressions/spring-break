package com.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
class WebJarsLocatorTest {
    @Test
    void webjarsLocatorCoreIsLoadableOnBoot35() {
        assertDoesNotThrow(
            () -> Class.forName("org.webjars.WebJarAssetLocator"),
            "WebJarAssetLocator should be on classpath on Boot 3.5. Removed in 4.0."
        );
    }
}
