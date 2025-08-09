# Filtering and Search Documentation

## Overview

The Product Catalog Service provides advanced filtering and search capabilities for both Products and Categories using JPA Specifications. This allows for dynamic, type-safe queries with support for pagination and sorting.

## Architecture

The filtering system is built using the **JPA Specifications pattern** which provides:
- **Type-safe queries** using JPA Criteria API
- **Dynamic query building** based on provided parameters
- **Composable filters** that can be combined with AND/OR logic
- **Null-safe parameter handling** to avoid unnecessary query conditions

### Components

1. **Specification Classes**: Define reusable filtering criteria
   - `ProductSpecification` - Product-specific filters
   - `CategorySpecification` - Category-specific filters

2. **Service Methods**: Business logic for search operations
   - `ProductService.search()` - Orchestrates product filtering
   - `CategoryService.search()` - Orchestrates category filtering

3. **REST Endpoints**: HTTP API for search operations
   - `GET /api/products/search` - Product search endpoint
   - `GET /api/categories/search` - Category search endpoint

## Product Search

### Available Filters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `name` | String | Case-insensitive LIKE search on product name | `name=laptop` |
| `status` | String | Exact match on product status | `status=ACTIVE` |
| `brand` | String | Case-insensitive LIKE search on brand | `brand=apple` |
| `sku` | String | Case-insensitive LIKE search on SKU | `sku=MBP-2024` |
| `categoryIds` | Set<UUID> | Filter by one or more category IDs | `categoryIds=uuid1,uuid2` |

### Predefined Filters

The `ProductSpecification` class also provides convenience methods:
- `isActive()` - Products with status = 'ACTIVE'
- `isNotDraft()` - Products with status != 'DRAFT'

### API Examples

```bash
# Basic search by name
GET /api/products/search?name=laptop

# Multiple filters
GET /api/products/search?name=laptop&status=ACTIVE&brand=apple

# Filter by categories with pagination
GET /api/products/search?categoryIds=uuid1,uuid2&page=0&size=10&sort=name,asc

# Find active products only
GET /api/products/search?status=ACTIVE&sort=createdAt,desc
```

### Response Format

```json
{
  "content": [
    {
      "id": "uuid",
      "name": "Product Name",
      "description": "Product Description",
      "sku": "SKU-123",
      "price": 999.99,
      "brand": "Brand Name",
      "status": "ACTIVE",
      "categories": [
        {
          "id": "uuid",
          "name": "Category Name",
          "slug": "category-slug"
        }
      ],
      "createdAt": "2025-08-09T10:00:00Z",
      "updatedAt": "2025-08-09T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "orders": [
        {
          "property": "name",
          "direction": "ASC"
        }
      ]
    }
  },
  "totalElements": 50,
  "totalPages": 3,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

## Category Search

### Available Filters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `name` | String | Case-insensitive LIKE search on category name | `name=electronics` |
| `slug` | String | Case-insensitive LIKE search on slug | `slug=laptop` |
| `parentId` | UUID | Filter by parent category ID | `parentId=uuid` |
| `isRoot` | Boolean | Filter root categories (true) or subcategories (false) | `isRoot=true` |

### Predefined Filters

The `CategorySpecification` class provides convenience methods:
- `isRootCategory()` - Categories with no parent
- `hasSubCategories()` - Categories that have child categories

### API Examples

```bash
# Find root categories
GET /api/categories/search?isRoot=true

# Search by name
GET /api/categories/search?name=electronics

# Find subcategories of a parent
GET /api/categories/search?parentId=electronics-uuid

# Complex search with pagination
GET /api/categories/search?name=comp&isRoot=false&page=0&size=5&sort=name,asc
```

## Pagination and Sorting

All search endpoints support Spring Data pagination and sorting:

### Pagination Parameters

- `page` (int) - Page number (0-based, default: 0)
- `size` (int) - Page size (default: 20)

### Sorting Parameters

- `sort` (string) - Property to sort by with optional direction
- Format: `property,direction` (e.g., `name,asc` or `createdAt,desc`)
- Multiple sort parameters supported: `sort=name,asc&sort=createdAt,desc`

### Default Sorting

- **Products**: Sorted by `name` ascending
- **Categories**: Sorted by `name` ascending

## Implementation Details

### Specification Pattern

The filtering system uses the JPA Specifications pattern for type-safe, dynamic queries:

```java
// Example: Building a complex product filter
Specification<Product> spec = null;

if (name != null && !name.trim().isEmpty()) {
    spec = addSpecification(spec, ProductSpecification.hasName(name));
}
if (status != null && !status.trim().isEmpty()) {
    spec = addSpecification(spec, ProductSpecification.hasStatus(status));
}

return productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);
```

### Repository Extensions

Repositories extend `JpaSpecificationExecutor` to support dynamic queries:

```java
public interface ProductRepository extends JpaRepository<Product, UUID>,
                                          JpaSpecificationExecutor<Product> {
    // Custom finder methods...
}
```

### Performance Considerations

1. **Database Indexes**: Ensure indexed columns for filtered fields
2. **LIKE Queries**: Use `%value%` pattern for contains searches
3. **Join Optimization**: Category filtering uses JOIN with proper fetch strategies
4. **Pagination**: Always use pagination for large result sets

## Error Handling

The search endpoints handle various error scenarios:

- **Invalid UUIDs**: Returns 400 Bad Request with validation errors
- **Invalid pagination parameters**: Returns 400 Bad Request
- **Database errors**: Returns 500 Internal Server Error
- **Empty results**: Returns empty page with proper pagination metadata

## Testing

### Unit Tests

Test specification methods individually:

```java
@Test
void shouldFilterProductsByName() {
    Specification<Product> spec = ProductSpecification.hasName("laptop");
    // Test specification logic
}
```

### Integration Tests

Test complete search functionality:

```java
@Test
void shouldSearchProductsWithMultipleFilters() {
    // Create test data
    // Execute search endpoint
    // Verify results and pagination
}
```

## Future Enhancements

Potential improvements for the filtering system:

1. **Full-text search** using PostgreSQL full-text search capabilities
2. **Price range filtering** with min/max parameters
3. **Date range filtering** for creation/update dates
4. **Advanced category hierarchy** filtering (descendants, ancestors)
5. **Search result highlighting** for matched terms
6. **Search analytics** and popular search terms tracking
7. **Elasticsearch integration** for advanced search capabilities

## Related Documentation

- [API Documentation](./API.md)
- [Database Schema](./DATABASE_SCHEMA.md)
- [Testing Guide](./TESTING.md)
