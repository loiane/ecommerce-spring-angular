# Technical Implementation Guide

## JPA Specifications Pattern Implementation

This document describes the technical implementation of advanced filtering and search capabilities using JPA Specifications in the Product Catalog Service.

## Overview

The JPA Specifications pattern provides a programmatic approach to building dynamic queries using the JPA Criteria API. This implementation allows for:

- **Type-safe queries** without string-based JPQL
- **Dynamic query composition** based on provided parameters
- **Reusable query criteria** across different contexts
- **Null-safe parameter handling** to avoid unnecessary WHERE clauses

## Architecture Components

### 1. Specification Classes

#### ProductSpecification.java

```java
public class ProductSpecification {

    // Text search with case-insensitive LIKE
    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
            );
    }

    // Exact match for status
    public static Specification<Product> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"), status);
    }

    // JOIN with categories for filtering
    public static Specification<Product> hasCategory(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> categoryJoin = root.join("categories");
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    // Complex OR condition for multiple categories
    public static Specification<Product> hasAnyCategory(Set<UUID> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> categoryJoin = root.join("categories");
            return categoryJoin.get("id").in(categoryIds);
        };
    }
}
```

#### CategorySpecification.java

```java
public class CategorySpecification {

    // Hierarchy filtering - root categories
    public static Specification<Category> isRootCategory() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNull(root.get("parentId"));
    }

    // Hierarchy filtering - subcategories
    public static Specification<Category> hasSubCategories() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNotNull(root.get("parentId"));
    }

    // Parent-child relationship filtering
    public static Specification<Category> hasParent(UUID parentId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("parentId"), parentId);
    }
}
```

### 2. Repository Extensions

Both repositories extend `JpaSpecificationExecutor` to support dynamic queries:

```java
public interface ProductRepository extends JpaRepository<Product, UUID>,
                                          JpaSpecificationExecutor<Product> {
    // Custom finder methods...
}

public interface CategoryRepository extends JpaRepository<Category, UUID>,
                                           JpaSpecificationExecutor<Category> {
    // Custom finder methods...
}
```

### 3. Service Layer Implementation

#### Dynamic Query Building

The service layer implements dynamic query building with proper null handling:

```java
@Service
public class ProductService {

    public Page<ProductResponse> search(String name, String status, String brand,
                                       String sku, Set<UUID> categoryIds,
                                       Pageable pageable) {
        Specification<Product> spec = null;

        // Build specification dynamically
        if (name != null && !name.trim().isEmpty()) {
            spec = addSpecification(spec, ProductSpecification.hasName(name));
        }
        if (status != null && !status.trim().isEmpty()) {
            spec = addSpecification(spec, ProductSpecification.hasStatus(status));
        }
        // ... additional filters

        // Execute query with or without filters
        if (spec == null) {
            return productRepository.findAll(pageable).map(ProductMapper::toResponse);
        }
        return productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);
    }

    // Helper method for specification composition
    private Specification<Product> addSpecification(Specification<Product> existing,
                                                   Specification<Product> newSpec) {
        return existing == null ? newSpec : existing.and(newSpec);
    }
}
```

### 4. Controller Layer

REST endpoints accept query parameters and delegate to service methods:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping("/search")
    public Page<ProductResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Set<UUID> categoryIds,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(name, status, brand, sku, categoryIds, pageable);
    }
}
```

## Implementation Patterns

### 1. Specification Composition

Specifications can be composed using logical operators:

```java
// AND composition
Specification<Product> spec = ProductSpecification.hasName("laptop")
    .and(ProductSpecification.hasStatus("ACTIVE"))
    .and(ProductSpecification.hasBrand("apple"));

// OR composition
Specification<Product> spec = ProductSpecification.hasName("laptop")
    .or(ProductSpecification.hasName("desktop"));

// NOT composition
Specification<Product> spec = Specification.not(ProductSpecification.hasStatus("DRAFT"));
```

### 2. Join Handling

For relationships, use JOIN to avoid N+1 query problems:

```java
public static Specification<Product> hasCategory(UUID categoryId) {
    return (root, query, criteriaBuilder) -> {
        // Creates INNER JOIN products p JOIN product_categories pc ON p.id = pc.product_id
        // JOIN categories c ON pc.category_id = c.id
        Join<Product, Category> categoryJoin = root.join("categories");
        return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
    };
}
```

### 3. Case-Insensitive Search

Implement case-insensitive text search:

```java
public static Specification<Product> hasName(String name) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")),
            "%" + name.toLowerCase() + "%"
        );
}
```

### 4. Null Safety

Handle null parameters gracefully:

```java
public Page<ProductResponse> search(String name, ...) {
    Specification<Product> spec = null;

    // Only add criteria if parameter is provided and not empty
    if (name != null && !name.trim().isEmpty()) {
        spec = addSpecification(spec, ProductSpecification.hasName(name));
    }

    // Handle case where no filters are applied
    if (spec == null) {
        return productRepository.findAll(pageable).map(ProductMapper::toResponse);
    }
    return productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);
}
```

## Performance Considerations

### 1. Database Indexes

Ensure appropriate database indexes for filtered columns:

```sql
-- Indexes for common search fields
CREATE INDEX idx_products_name ON products(lower(name));
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_brand ON products(lower(brand));
CREATE INDEX idx_products_sku ON products(lower(sku));

-- Indexes for category relationships
CREATE INDEX idx_product_categories_product_id ON product_categories(product_id);
CREATE INDEX idx_product_categories_category_id ON product_categories(category_id);
```

### 2. Query Optimization

The JPA Specifications generate optimized SQL queries:

```sql
-- Example generated query for product search
SELECT p.* FROM products p
LEFT JOIN product_categories pc ON p.id = pc.product_id
LEFT JOIN categories c ON pc.category_id = c.id
WHERE LOWER(p.name) LIKE '%laptop%'
  AND p.status = 'ACTIVE'
  AND c.id IN ('uuid1', 'uuid2')
ORDER BY p.name ASC
LIMIT 20 OFFSET 0;
```

### 3. Pagination Performance

Always use pagination to avoid loading large result sets:

```java
// Default pagination settings
@PageableDefault(size = 20, sort = "name")
```

## Testing Strategies

### 1. Unit Testing Specifications

Test specification logic in isolation:

```java
@Test
void shouldCreateNameFilterSpecification() {
    // Given
    String searchName = "laptop";

    // When
    Specification<Product> spec = ProductSpecification.hasName(searchName);

    // Then
    assertNotNull(spec);
    // Test with mock CriteriaBuilder and Root
}
```

### 2. Integration Testing

Test complete search functionality with real database:

```java
@SpringBootTest
@Testcontainers
class ProductSearchIntegrationTest {

    @Test
    void shouldSearchProductsByMultipleFilters() {
        // Given: Create test data
        Product laptop = createProduct("MacBook Pro", "ACTIVE", "Apple");

        // When: Execute search
        Page<ProductResponse> results = productService.search(
            "macbook", "ACTIVE", "apple", null, null,
            PageRequest.of(0, 10)
        );

        // Then: Verify results
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).name()).contains("MacBook");
    }
}
```

## Error Handling

### 1. Invalid Parameters

Handle invalid search parameters gracefully:

```java
// Service layer validation
if (categoryIds != null && categoryIds.contains(null)) {
    throw new IllegalArgumentException("Category IDs cannot contain null values");
}
```

### 2. Database Errors

Specification errors are translated to appropriate HTTP responses:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataAccessException ex) {
        // Log error and return appropriate response
        return ResponseEntity.status(500)
            .body(new ErrorResponse("Search temporarily unavailable"));
    }
}
```

## Best Practices

### 1. Specification Naming

Use descriptive method names that clearly indicate the filter purpose:

```java
// Good
ProductSpecification.hasName(String name)
ProductSpecification.isActive()
ProductSpecification.belongsToCategory(UUID categoryId)

// Avoid
ProductSpecification.filter1(String value)
ProductSpecification.check(UUID id)
```

### 2. Parameter Validation

Validate parameters before building specifications:

```java
public static Specification<Product> hasName(String name) {
    if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Name cannot be null or empty");
    }
    return (root, query, criteriaBuilder) -> // ... implementation
}
```

### 3. Reusability

Design specifications to be reusable across different contexts:

```java
// Reusable specifications
public static final Specification<Product> IS_ACTIVE =
    (root, query, cb) -> cb.equal(root.get("status"), "ACTIVE");

public static final Specification<Product> IS_NOT_DRAFT =
    (root, query, cb) -> cb.notEqual(root.get("status"), "DRAFT");

// Combine in different ways
Specification<Product> publicProducts = IS_ACTIVE.and(IS_NOT_DRAFT);
```

### 4. Documentation

Document complex specifications with examples:

```java
/**
 * Creates a specification to filter products by category.
 * Uses INNER JOIN to fetch products that belong to the specified category.
 *
 * @param categoryId The UUID of the category to filter by
 * @return Specification that filters products by category
 *
 * @example
 * // Find all products in Electronics category
 * Specification<Product> spec = ProductSpecification.hasCategory(electronicsId);
 * List<Product> electronics = repository.findAll(spec);
 */
public static Specification<Product> hasCategory(UUID categoryId) {
    // Implementation...
}
```

## Future Enhancements

### 1. Advanced Search Features

- **Full-text search**: Integration with PostgreSQL full-text search
- **Fuzzy matching**: Approximate string matching for typos
- **Search ranking**: Relevance scoring for search results
- **Search suggestions**: Auto-complete and did-you-mean features

### 2. Performance Optimizations

- **Query caching**: Cache frequent search queries
- **Result caching**: Cache search results for identical parameters
- **Async search**: Non-blocking search for large datasets
- **Search analytics**: Track popular searches and optimize accordingly

### 3. Advanced Filtering

- **Range filters**: Date ranges, price ranges
- **Faceted search**: Category facets, brand facets
- **Geographic search**: Location-based filtering
- **Custom attributes**: Dynamic product attribute filtering
