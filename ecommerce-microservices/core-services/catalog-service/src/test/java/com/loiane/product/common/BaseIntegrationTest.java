package com.loiane.product.common;

import com.loiane.product.TestcontainersConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base integration test configuration that sets up TestContainers with PostgreSQL
 * and provides common test configuration for integration tests.
 * 
 * This annotation can be used on integration test classes to automatically:
 * - Start a PostgreSQL container using TestContainers
 * - Configure Spring Boot for integration testing
 * - Set up MockMvc for web layer testing
 * - Set up transactional test rollback for data cleanup
 * - Configure test-specific properties
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "logging.level.org.hibernate.SQL=DEBUG",
    "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE"
})
@Transactional
@DirtiesContext
public @interface BaseIntegrationTest {
}