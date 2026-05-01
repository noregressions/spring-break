package com.example;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests LIKE wildcard behaviour in Spring Data JPA derived queries.
 *
 * On Spring Boot 3.5 (Spring Data JPA 3.x): The % character inside a derived
 * query parameter is passed through unescaped to SQL. findByNameContaining("100%")
 * generates LIKE '%100%%' — the embedded % acts as a wildcard, matching any name
 * containing "100" followed by anything. Returns 3 results: "100% Organic Juice",
 * "100% Cotton Shirt", AND "1000 Islands Dressing".
 *
 * On Spring Boot 4.0 (Spring Data JPA 4.x): Derived queries now escape special
 * characters (%, _) in bind parameters and add an ESCAPE clause.
 * findByNameContaining("100%") generates LIKE '%100\%%' ESCAPE '\' — the % is
 * literal, matching only names containing the literal string "100%". Returns
 * only 2 results. Test fails.
 *
 * This is a Tier 3 "Different Results" failure — no error, just different output.
 */
@SpringBootTest
@Transactional
class LikePatternEscapingTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.save(new Product("100% Organic Juice"));
        productRepository.save(new Product("100% Cotton Shirt"));
        productRepository.save(new Product("1000 Islands Dressing"));
        productRepository.save(new Product("Regular Product"));
    }

    @Test
    void percentInParameterShouldActAsWildcard() {
        // "100%" → on 3.5 the embedded % leaks as a SQL wildcard → matches 3 items
        // On 4.0, Spring Data escapes the %, so only literal "100%" matches → 2 items
        List<Product> results = productRepository.findByNameContaining("100%");

        assertEquals(3, results.size(),
                "Expected 3 results (% leaks as SQL wildcard in derived query). "
                + "If only 2, Spring Data is escaping the % in LIKE parameters. "
                + "Got " + results.size());
    }
}
