# Quick Reference Guide

## Search & Filter Endpoints

### Product Search
```bash
GET /api/products/search
```

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `name` | String | Product name contains (case-insensitive) | `?name=laptop` |
| `status` | String | Exact status match | `?status=ACTIVE` |
| `brand` | String | Brand name contains (case-insensitive) | `?brand=apple` |
| `sku` | String | SKU contains (case-insensitive) | `?sku=MBP-2024` |
| `categoryIds` | UUID[] | Products in any of these categories | `?categoryIds=uuid1,uuid2` |

### Category Search
```bash
GET /api/categories/search
```

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `name` | String | Category name contains (case-insensitive) | `?name=electronics` |
| `slug` | String | Slug contains (case-insensitive) | `?slug=laptop` |
| `parentId` | UUID | Categories with specific parent | `?parentId=uuid` |
| `isRoot` | Boolean | Root categories (true) or subcategories (false) | `?isRoot=true` |

### Pagination & Sorting

All endpoints support:
- `page` (int) - Page number (0-based, default: 0)
- `size` (int) - Page size (default: 20)
- `sort` (string) - Sort by property with direction (default: name,asc)

## Quick Examples

### Basic Search
```bash
# Find laptops
curl "http://localhost:8081/api/products/search?name=laptop"

# Find active products
curl "http://localhost:8081/api/products/search?status=ACTIVE"

# Find Apple products
curl "http://localhost:8081/api/products/search?brand=apple"
```

### Combined Filters
```bash
# Active Apple laptops
curl "http://localhost:8081/api/products/search?name=laptop&status=ACTIVE&brand=apple"

# Products in specific categories
curl "http://localhost:8081/api/products/search?categoryIds=uuid1,uuid2"
```

### Pagination & Sorting
```bash
# Second page, 10 items per page
curl "http://localhost:8081/api/products/search?name=laptop&page=1&size=10"

# Sort by price descending
curl "http://localhost:8081/api/products/search?sort=price,desc"

# Multiple sort criteria
curl "http://localhost:8081/api/products/search?sort=price,desc&sort=name,asc"
```

### Category Search
```bash
# Find root categories
curl "http://localhost:8081/api/categories/search?isRoot=true"

# Find electronics subcategories
curl "http://localhost:8081/api/categories/search?name=electronics&isRoot=false"

# Find categories by parent
curl "http://localhost:8081/api/categories/search?parentId=electronics-uuid"
```

## Response Format

All search endpoints return paginated results:

```json
{
  "content": [ /* Array of results */ ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "orders": [{"property": "name", "direction": "ASC"}]
    }
  },
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

## Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Search successful |
| 400 | Bad Request - Invalid parameters |
| 500 | Internal Server Error - Server error |

## Development URLs

- **Application**: http://localhost:8081
- **Health Check**: http://localhost:8081/actuator/health
- **Database Admin**: http://localhost:8085 (Adminer)

## Common Patterns

### Building Specifications
```java
// In your service class
Specification<Product> spec = null;

if (name != null && !name.trim().isEmpty()) {
    spec = addSpecification(spec, ProductSpecification.hasName(name));
}

private Specification<Product> addSpecification(Specification<Product> existing,
                                               Specification<Product> newSpec) {
    return existing == null ? newSpec : existing.and(newSpec);
}
```

### Custom Specifications
```java
// For exact match
public static Specification<Product> hasStatus(String status) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("status"), status);
}

// For LIKE search
public static Specification<Product> hasName(String name) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")),
            "%" + name.toLowerCase() + "%"
        );
}

// For JOIN operations
public static Specification<Product> hasCategory(UUID categoryId) {
    return (root, query, criteriaBuilder) -> {
        Join<Product, Category> categoryJoin = root.join("categories");
        return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
    };
}
```

### Testing Search Endpoints
```bash
# Test with curl
curl -X GET "http://localhost:8081/api/products/search?name=test" \
  -H "Content-Type: application/json"

# Test with HTTPie
http GET localhost:8081/api/products/search name==laptop status==ACTIVE

# Test with Postman
# Import the API collection from docs/postman/
```

## Troubleshooting

### Common Issues

1. **Empty Results**: Check if filters are too restrictive
2. **Slow Queries**: Ensure database indexes exist for filtered columns
3. **Invalid UUID**: Ensure UUID format is correct for categoryIds/parentId
4. **Page Not Found**: Check if page number exceeds totalPages

### Debug Tips
```bash
# Check application logs
docker logs product-postgres

# Verify database connection
curl http://localhost:8081/actuator/health

# Check database content (Adminer)
# Visit http://localhost:8085
# Server: postgres, User: product, Password: product, Database: product
```

## Performance Tips

1. **Use Indexes**: Ensure database indexes on filtered columns
2. **Limit Page Size**: Use reasonable page sizes (10-50 items)
3. **Specific Filters**: Use specific filters to reduce result sets
4. **Cache Results**: Consider caching for frequent searches

## Extensions

### Adding New Filters

1. **Add Specification Method**:
```java
public static Specification<Product> hasPriceRange(BigDecimal min, BigDecimal max) {
    return (root, query, criteriaBuilder) -> {
        if (min != null && max != null) {
            return criteriaBuilder.between(root.get("price"), min, max);
        } else if (min != null) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), min);
        } else if (max != null) {
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), max);
        }
        return null;
    };
}
```

2. **Update Service Method**:
```java
public Page<ProductResponse> search(..., BigDecimal minPrice, BigDecimal maxPrice, ...) {
    // Add to specification building logic
    if (minPrice != null || maxPrice != null) {
        spec = addSpecification(spec, ProductSpecification.hasPriceRange(minPrice, maxPrice));
    }
}
```

3. **Update Controller**:
```java
@GetMapping("/search")
public Page<ProductResponse> search(
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        // ... other parameters
) {
    return service.search(..., minPrice, maxPrice, ...);
}
```
