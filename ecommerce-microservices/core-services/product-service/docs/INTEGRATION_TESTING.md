# Integration Testing Documentation

## Overview

This document describes the comprehensive integration testing strategy implemented for the Catalog Service using TestContainers. Our integration tests provide end-to-end validation of the API endpoints with real database interactions.

## Architecture

### TestContainers Setup

Our integration tests use [TestContainers](https://testcontainers.com/) to provide isolated, reproducible testing environments:

- **Base Class**: `BaseIntegrationTest` provides common configuration for all integration tests
- **Database**: PostgreSQL 16-alpine container for realistic database interactions
- **Isolation**: Each test class uses a fresh database container via `@DirtiesContext`
- **Spring Boot**: Full application context with `@SpringBootTest(webEnvironment = RANDOM_PORT)`

### Integration Test Structure

```
src/test/java/com/loiane/product/integration/
├── BaseIntegrationTest.java          # Common TestContainers configuration
├── CategoryIntegrationTest.java      # Category API integration tests
├── ProductIntegrationTest.java       # Product API integration tests
└── RestPageImpl.java                # Helper for paginated response deserialization
```

## Category API Integration Tests

### Coverage Areas

1. **List Categories** (`GET /api/categories`)
   - Paginated category listing
   - Sorting by name, creation date
   - Pagination parameter validation
   - Sample data verification

2. **Search Categories** (`GET /api/categories/search`)
   - Search by name (case-insensitive)
   - Search by slug
   - Filter root categories only (`isRoot=true`)
   - Filter subcategories by parent ID
   - Empty result handling
   - Multiple search criteria combination

3. **Get Category by ID** (`GET /api/categories/{id}`)
   - Successful category retrieval
   - Parent-child relationship verification
   - 404 handling for non-existent categories

4. **Create Category** (`POST /api/categories`)
   - Root category creation
   - Subcategory creation with parent assignment
   - Input validation (required fields)
   - Duplicate slug conflict handling (409 status)
   - Location header verification

5. **Update Category** (`PUT /api/categories/{id}`)
   - Category name and slug updates
   - Parent reassignment
   - 404 handling for non-existent categories
   - Validation error handling

6. **Delete Category** (`DELETE /api/categories/{id}`)
   - Successful category deletion
   - 404 handling for non-existent categories
   - Verification of actual deletion

### Key Test Scenarios

- **Sample Data Integration**: Tests verify interaction with the 25 sample categories
- **Hierarchical Relationships**: Parent-child category relationships are thoroughly tested
- **Search Functionality**: Comprehensive search parameter testing
- **Error Handling**: HTTP status codes and error responses validation

## Product API Integration Tests

### Coverage Areas

1. **List Products** (`GET /api/products`)
   - Paginated product listing with sample data verification
   - Sorting by name, creation date
   - Pagination parameter validation
   - Category relationship loading

2. **Search Products** (`GET /api/products/search`)
   - Search by name (case-insensitive, partial match)
   - Search by brand
   - Search by status (ACTIVE, INACTIVE, DRAFT)
   - Search by SKU
   - Search by category IDs
   - Combined search criteria
   - Empty result handling

3. **Get Product by ID** (`GET /api/products/{id}`)
   - Successful product retrieval
   - Category information verification
   - 404 handling for non-existent products

4. **Create Product** (`POST /api/products`)
   - Product creation with single category
   - Product creation with multiple categories
   - Input validation (required fields, constraints)
   - Duplicate SKU conflict handling (409 status)
   - Location header verification

5. **Update Product** (`PUT /api/products/{id}`)
   - Product field updates (name, SKU, description, brand, status)
   - Category assignment changes
   - 404 handling for non-existent products

6. **Delete Product** (`DELETE /api/products/{id}`)
   - Successful product deletion
   - Category relationship cleanup
   - 404 handling for non-existent products
   - Verification of actual deletion

### Key Test Scenarios

- **Sample Data Integration**: Tests work with 25 sample products from various categories
- **Multi-Category Products**: Tests verify products can belong to multiple categories
- **Advanced Search**: Complex search scenarios with multiple parameters
- **Data Integrity**: Foreign key relationships and constraints validation

## Supporting Infrastructure

### BaseIntegrationTest

Provides common configuration for all integration tests:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
    "spring.flyway.clean-disabled=false",
    "spring.jpa.hibernate.ddl-auto=none"
})
public abstract class BaseIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("catalog_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

### RestPageImpl

Helper class for deserializing Spring Data `Page` responses in integration tests:

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPageImpl<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPageImpl(@JsonProperty("content") List<T> content,
                       @JsonProperty("number") int number,
                       @JsonProperty("size") int size,
                       @JsonProperty("totalElements") long totalElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }
}
```

## Test Execution Strategy

### Isolation

- Each test class runs with a fresh database container (`@DirtiesContext`)
- Tests within a class share the same container for performance
- Database is reset between test classes, not between individual tests

### Performance Optimization

- Container reuse within test classes
- Flyway migrations run once per container
- Sample data loaded once per container
- Parallel test execution where possible

### Reliability

- Real database interactions (not mocked)
- Complete Spring application context
- Actual HTTP requests via `TestRestTemplate`
- Database constraints and foreign keys enforced

## Running Integration Tests

### Individual Test Classes
```bash
mvn test -Dtest="CategoryIntegrationTest"
mvn test -Dtest="ProductIntegrationTest"
```

### All Integration Tests
```bash
mvn test -Dtest="*IntegrationTest"
```

### Full Test Suite
```bash
mvn test
```

## Test Metrics

After implementing integration tests, our test coverage includes:

- **Total Tests**: 231 tests (192 unit tests + 39 integration tests)
- **Integration Test Coverage**:
  - Category API: 19 integration tests
  - Product API: 20 integration tests
- **API Endpoint Coverage**: 100% of REST endpoints tested
- **Error Scenario Coverage**: All major error conditions tested
- **Sample Data Integration**: Full verification with realistic test data

## Benefits

1. **End-to-End Validation**: Tests the complete request/response cycle
2. **Real Database Interactions**: Catches SQL and constraint issues
3. **API Contract Verification**: Ensures API responses match specifications
4. **Sample Data Validation**: Verifies migrations and sample data work correctly
5. **Regression Prevention**: Comprehensive test suite prevents breaking changes
6. **Documentation**: Tests serve as living documentation of API behavior

## Best Practices

1. **Test Isolation**: Each test can run independently
2. **Clear Test Names**: Descriptive test method names explain the scenario
3. **Comprehensive Assertions**: Multiple assertions per test verify complete behavior
4. **Error Testing**: Both success and failure scenarios are tested
5. **Sample Data Usage**: Tests work with realistic data scenarios
6. **Performance Considerations**: Container reuse and efficient test design

This integration testing strategy ensures our Catalog Service APIs are thoroughly tested with realistic scenarios, providing confidence in the system's reliability and correctness.
