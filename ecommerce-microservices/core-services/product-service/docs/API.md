# Product Catalog Service - API Documentation

## Base URL
```
http://localhost:8081/api
```

## Products API

### List All Products
```http
GET /api/products
```

**Parameters:**
- `page` (optional, int): Page number (0-based, default: 0)
- `size` (optional, int): Page size (default: 20)
- `sort` (optional, string): Sort by property (default: name)

**Response:** `200 OK`
```json
{
  "content": [Product],
  "pageable": { ... },
  "totalElements": 100,
  "totalPages": 5
}
```

### Search Products (NEW)
```http
GET /api/products/search
```

**Parameters:**
- `name` (optional, string): Filter by name (case-insensitive LIKE)
- `status` (optional, string): Filter by status (exact match)
- `brand` (optional, string): Filter by brand (case-insensitive LIKE)
- `sku` (optional, string): Filter by SKU (case-insensitive LIKE)
- `categoryIds` (optional, UUID[]): Filter by category IDs (comma-separated)
- `page` (optional, int): Page number (0-based, default: 0)
- `size` (optional, int): Page size (default: 20)
- `sort` (optional, string): Sort by property (default: name)

**Examples:**
```bash
# Search by name
GET /api/products/search?name=laptop

# Multiple filters
GET /api/products/search?name=laptop&status=ACTIVE&brand=apple

# Filter by categories
GET /api/products/search?categoryIds=uuid1,uuid2&sort=price,desc

# Pagination
GET /api/products/search?name=phone&page=1&size=10&sort=createdAt,desc
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "MacBook Pro 14-inch",
      "description": "Apple MacBook Pro with M3 chip",
      "sku": "MBP-14-M3-2024",
      "price": 1999.99,
      "brand": "Apple",
      "status": "ACTIVE",
      "categories": [
        {
          "id": "660e8400-e29b-41d4-a716-446655440000",
          "name": "Laptops",
          "slug": "laptops"
        }
      ],
      "createdAt": "2025-08-09T10:00:00Z",
      "updatedAt": "2025-08-09T12:30:00Z"
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
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

### Get Product by ID
```http
GET /api/products/{id}
```

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "MacBook Pro 14-inch",
  "description": "Apple MacBook Pro with M3 chip",
  "sku": "MBP-14-M3-2024",
  "price": 1999.99,
  "brand": "Apple",
  "status": "ACTIVE",
  "categories": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "name": "Laptops",
      "slug": "laptops"
    }
  ],
  "createdAt": "2025-08-09T10:00:00Z",
  "updatedAt": "2025-08-09T12:30:00Z"
}
```

### Create Product
```http
POST /api/products
```

**Request Body:**
```json
{
  "name": "MacBook Pro 14-inch",
  "description": "Apple MacBook Pro with M3 chip",
  "sku": "MBP-14-M3-2024",
  "price": 1999.99,
  "brand": "Apple",
  "status": "ACTIVE",
  "categoryIds": ["660e8400-e29b-41d4-a716-446655440000"]
}
```

**Response:** `201 Created`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "MacBook Pro 14-inch",
  "description": "Apple MacBook Pro with M3 chip",
  "sku": "MBP-14-M3-2024",
  "price": 1999.99,
  "brand": "Apple",
  "status": "ACTIVE",
  "categories": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "name": "Laptops",
      "slug": "laptops"
    }
  ],
  "createdAt": "2025-08-09T10:00:00Z",
  "updatedAt": "2025-08-09T10:00:00Z"
}
```

### Update Product
```http
PUT /api/products/{id}
```

**Request Body:** Same as Create Product

**Response:** `200 OK` (Same format as Get Product)

### Delete Product
```http
DELETE /api/products/{id}
```

**Response:** `204 No Content`

## Categories API

### List All Categories
```http
GET /api/categories
```

**Parameters:**
- `page` (optional, int): Page number (0-based, default: 0)
- `size` (optional, int): Page size (default: 20)
- `sort` (optional, string): Sort by property (default: name)

**Response:** `200 OK`
```json
{
  "content": [Category],
  "pageable": { ... },
  "totalElements": 50,
  "totalPages": 3
}
```

### Search Categories (NEW)
```http
GET /api/categories/search
```

**Parameters:**
- `name` (optional, string): Filter by name (case-insensitive LIKE)
- `slug` (optional, string): Filter by slug (case-insensitive LIKE)
- `parentId` (optional, UUID): Filter by parent category ID
- `isRoot` (optional, boolean): Filter root categories (true) or subcategories (false)
- `page` (optional, int): Page number (0-based, default: 0)
- `size` (optional, int): Page size (default: 20)
- `sort` (optional, string): Sort by property (default: name)

**Examples:**
```bash
# Find root categories
GET /api/categories/search?isRoot=true

# Search by name
GET /api/categories/search?name=electronics

# Find subcategories
GET /api/categories/search?parentId=660e8400-e29b-41d4-a716-446655440000

# Complex search
GET /api/categories/search?name=comp&isRoot=false&sort=name,asc
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "name": "Electronics",
      "slug": "electronics",
      "description": "Electronic devices and gadgets",
      "parentId": null,
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
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

### Get Category by ID
```http
GET /api/categories/{id}
```

**Response:** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Electronics",
  "slug": "electronics",
  "description": "Electronic devices and gadgets",
  "parentId": null,
  "createdAt": "2025-08-09T10:00:00Z",
  "updatedAt": "2025-08-09T10:00:00Z"
}
```

### Create Category
```http
POST /api/categories
```

**Request Body:**
```json
{
  "name": "Electronics",
  "slug": "electronics",
  "description": "Electronic devices and gadgets",
  "parentId": null
}
```

**Response:** `201 Created`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "name": "Electronics",
  "slug": "electronics",
  "description": "Electronic devices and gadgets",
  "parentId": null,
  "createdAt": "2025-08-09T10:00:00Z",
  "updatedAt": "2025-08-09T10:00:00Z"
}
```

### Update Category
```http
PUT /api/categories/{id}
```

**Request Body:** Same as Create Category

**Response:** `200 OK` (Same format as Get Category)

### Delete Category
```http
DELETE /api/categories/{id}
```

**Response:** `204 No Content`

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "Name is required"
    },
    {
      "field": "price",
      "message": "Price must be positive"
    }
  ]
}
```

### Not Found (404 Not Found)
```json
{
  "message": "Product not found: 550e8400-e29b-41d4-a716-446655440000"
}
```

### Conflict (409 Conflict)
```json
{
  "message": "SKU already exists: MBP-14-M3-2024"
}
```

### Internal Server Error (500 Internal Server Error)
```json
{
  "message": "An unexpected error occurred"
}
```

## Data Models

### Product Model
```json
{
  "id": "UUID",
  "name": "string (required, max 255)",
  "description": "string (optional, max 1000)",
  "sku": "string (required, unique, max 100)",
  "price": "decimal (required, positive)",
  "brand": "string (required, max 100)",
  "status": "string (required, enum: ACTIVE|DRAFT|DISCONTINUED)",
  "categories": "CategoryResponse[]",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Category Model
```json
{
  "id": "UUID",
  "name": "string (required, max 255)",
  "slug": "string (required, unique, max 255)",
  "description": "string (optional, max 500)",
  "parentId": "UUID (optional)",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

## Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Resource deleted successfully |
| 400 | Bad Request - Invalid request data |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 500 | Internal Server Error - Server error |

## Rate Limiting

Currently no rate limiting is implemented. Consider implementing rate limiting for production use.

## Authentication

Currently no authentication is required. Consider implementing authentication for production use.

## Pagination

All list endpoints support pagination with the following parameters:
- `page`: Page number (0-based)
- `size`: Number of items per page
- `sort`: Sort criteria (property,direction)

Default pagination settings:
- Page size: 20
- Sort: name,asc
