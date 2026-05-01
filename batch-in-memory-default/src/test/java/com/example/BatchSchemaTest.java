package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Master list: 2.6 — Spring Batch in-memory default */
@SpringBootTest
public class BatchSchemaTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void batchTablesShouldExistOnBoot35() {
        // In Boot 3.5, spring-boot-starter-batch includes JDBC support and initializes schema by default
        // (with spring.batch.jdbc.initialize-schema=always)
        // In Boot 4.0, it uses ResourcelessJobRepository by default and ignores JDBC properties
        // unless spring-boot-starter-batch-jdbc is added.
        
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BATCH_JOB_INSTANCE'", 
            Integer.class
        );
        
        assertTrue(count != null && count > 0, 
            "Batch metadata tables should have been initialized on Boot 3.5. " +
            "On Boot 4.0, they are not created by default because it uses in-memory repository.");
    }
}
