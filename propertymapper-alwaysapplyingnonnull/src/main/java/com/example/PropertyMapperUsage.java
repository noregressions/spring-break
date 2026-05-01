package com.example;

import org.springframework.boot.context.properties.PropertyMapper;

public class PropertyMapperUsage {
    public static void useRemovedMethod() {
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
    }
}
