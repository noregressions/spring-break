package com.example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.test.autoconfigure.properties.PropertyMapping;

@Retention(RetentionPolicy.RUNTIME)
@PropertyMapping("test.custom")
public @interface CustomTestAnnotation {
    String value() default "";
}
