package com.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * DTO using custom serializer and deserializer for its price field.
 * On Boot 4.0 this file itself compiles (annotation classes survive),
 * but MoneySerializer and MoneyDeserializer do not.
 */
public class Product {

    private String name;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private Long priceCents;

    public Product() {}

    public Product(String name, Long priceCents) {
        this.name = name;
        this.priceCents = priceCents;
    }

    public String getName() { return name; }
    public Long getPriceCents() { return priceCents; }
    public void setName(String name) { this.name = name; }
    public void setPriceCents(Long priceCents) { this.priceCents = priceCents; }
}
