package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that @DataJpaTest auto-configures JPA infrastructure and that the
 * test-slice annotations load from their current package locations.
 *
 * Spring Boot 3.5: @DataJpaTest auto-configuration metadata is at the standard
 * location. The slice correctly loads JPA + H2 + repository beans. Tests pass.
 *
 * Spring Boot 4.0: Test-slice auto-configuration metadata is relocated. Custom
 * test slices referencing old package paths or old META-INF entries will fail
 * with missing bean definitions or ClassNotFoundException.
 *
 * This test uses @DataJpaTest with the old-style AutoConfigureDataJpa annotation
 * path. On 3.5 it works fine; on 4.0 the relocated packages break resolution.
 */
@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class TestSliceRelocatedTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Test
    void dataJpaTestSliceShouldAutoConfigureRepository() {
        // Verify the repository bean is available via @DataJpaTest auto-config
        assertNotNull(userRepository, "UserRepository should be auto-configured by @DataJpaTest");

        // Verify we can perform basic CRUD — proves the full JPA slice loaded
        User saved = userRepository.save(new User("Alice", "alice@example.com"));
        assertNotNull(saved.getId(), "Saved entity should have a generated ID");

        User found = userRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found, "Should find the saved user by ID");
        assertEquals("Alice", found.getName());
    }

    @Test
    void entityManagerFactoryShouldBePresent() {
        // On 3.5, @DataJpaTest correctly locates the auto-configuration for
        // LocalContainerEntityManagerFactoryBean via the standard metadata path.
        // On 4.0, if the metadata path changes, this bean won't be found.
        assertTrue(context.containsBean("entityManagerFactory"),
            "entityManagerFactory bean should be auto-configured by @DataJpaTest. "
                + "If missing, the test-slice auto-configuration metadata may have been relocated.");
    }
}
