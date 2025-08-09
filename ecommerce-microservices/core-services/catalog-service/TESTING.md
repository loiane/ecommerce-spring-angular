# Testing Guide for Product Service

This document provides instructions for running the different types of tests in the Product Service.

## Test Structure

The project includes two types of tests:

- **Unit Tests**: Fast tests that mock dependencies and test individual components in isolation
- **Integration Tests**: Comprehensive tests that use TestContainers with real PostgreSQL database

## Prerequisites

- **Docker**: Required for integration tests (TestContainers will start PostgreSQL containers)
- **Java 17**: Required for running the application and tests
- **Maven**: For building and running tests

## Running Tests

### Unit Tests Only

To run only the unit tests (fast, no database required):

```bash
./mvnw test
```

This runs **224 unit tests** and excludes integration tests. These tests:
- Use Mockito to mock dependencies
- Test individual components (controllers, services, mappers, specifications)
- Run in seconds
- Do not require Docker or external dependencies

### Integration Tests Only

To run only the integration tests:

```bash
./mvnw failsafe:integration-test
```

This runs **32 integration tests** using TestContainers:
- **ProductController Integration Tests** (15 tests): Full CRUD operations with database
- **CategoryController Integration Tests** (17 tests): Full CRUD operations with database

### All Tests (Unit + Integration)

To run all tests including integration tests:

```bash
./mvnw verify
```

This command:
1. Runs all 224 unit tests
2. Runs all 32 integration tests
3. Generates code coverage reports
4. Validates coverage thresholds (80% instruction, 70% branch coverage)

## Integration Test Details

### TestContainers Configuration

Integration tests use TestContainers with:
- **PostgreSQL 16-alpine**: Matches production Docker image
- **Automatic cleanup**: Containers are destroyed after each test class
- **Isolated environments**: Each test gets a fresh database

### Test Coverage

Integration tests cover:

#### ProductController Tests
- **CRUD Operations**: Create, Read, Update, Delete products
- **Search & Filtering**: By name, brand, status, category, SKU
- **Pagination**: Custom page size, sorting, navigation
- **Validation**: Input validation and error handling
- **Relationships**: Product-category associations

#### CategoryController Tests
- **CRUD Operations**: Create, Read, Update, Delete categories
- **Hierarchical Structure**: Root categories and sub-categories
- **Search & Filtering**: By name, slug, parent ID
- **Pagination**: Custom page size, sorting, navigation
- **Validation**: Input validation and error handling

### Database Schema

Integration tests use Hibernate's `create-drop` strategy:
- Tables are created automatically from JPA entities
- Data is cleaned up between tests using `@Transactional` rollback
- Flyway is disabled in test environment to avoid conflicts

## Continuous Integration

For CI environments, use:

```bash
./mvnw verify
```

This ensures both unit and integration tests pass, and all code quality checks are met.

## Troubleshooting

### Docker Issues

If integration tests fail with Docker-related errors:

1. **Check Docker is running**:
   ```bash
   docker ps
   ```

2. **Pull PostgreSQL image manually** (if needed):
   ```bash
   docker pull postgres:16-alpine
   ```

3. **Clean up containers** (if needed):
   ```bash
   docker system prune
   ```

### Memory Issues

If tests fail with OutOfMemoryError:

1. **Increase heap size**:
   ```bash
   export MAVEN_OPTS="-Xmx2g"
   ./mvnw verify
   ```

### Test-specific Issues

- **Port conflicts**: TestContainers uses random ports, so this should not be an issue
- **Database cleanup**: Tests use `@Transactional` with rollback for automatic cleanup
- **Container reuse**: Tests create fresh containers to ensure isolation

## Code Coverage

The project maintains:
- **80% minimum instruction coverage**
- **70% minimum branch coverage**

View coverage reports in: `target/site/jacoco/index.html`

## Best Practices

1. **Run unit tests frequently** during development (fast feedback)
2. **Run integration tests before commits** (comprehensive validation)
3. **Use `./mvnw verify` before releases** (full validation)
4. **Check coverage reports** to identify untested code
5. **Keep integration tests focused** on critical business flows