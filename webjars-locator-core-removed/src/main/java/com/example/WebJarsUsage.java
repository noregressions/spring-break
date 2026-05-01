package com.example;
import org.webjars.WebJarAssetLocator;
public class WebJarsUsage {
    public static String getWebJarPath() {
        try {
            // Using reflection to avoid compile-time dependency on core in some scenarios, 
            // but here we use it directly to demonstrate Tier 1 failure.
            WebJarAssetLocator locator = new WebJarAssetLocator();
            return locator.getFullPath("jquery", "jquery.min.js");
        } catch (Exception e) {
            return null;
        }
    }
}
