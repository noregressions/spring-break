package com.example;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Spring Data JPA derived query — "Containing" builds:
     *   WHERE p.name LIKE '%' || :fragment || '%'
     *
     * On Boot 3.5 (Spring Data JPA 3.x): The % inside the fragment value is
     *   passed through unescaped to SQL. findByNameContaining("100%") becomes
     *   LIKE '%100%%' — the embedded % acts as a wildcard, matching ANY name
     *   containing "100" followed by anything.
     *
     * On Boot 4.0 (Spring Data JPA 4.x): Derived queries now escape special
     *   characters (%, _) in bind parameters and add an ESCAPE clause.
     *   findByNameContaining("100%") becomes LIKE '%100\%%' ESCAPE '\' —
     *   the % is literal, matching only names containing the literal "100%".
     */
    List<Product> findByNameContaining(String fragment);
}
