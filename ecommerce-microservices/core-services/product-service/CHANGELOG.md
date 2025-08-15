# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Advanced filtering and search capabilities using JPA Specifications
- Product search endpoint `/api/products/search` with multiple filter options
- Category search endpoint `/api/categories/search` with hierarchy filtering
- Comprehensive documentation for filtering and search features
- API documentation with detailed examples
- Project README with setup and usage instructions

### Changed
- Service renamed from "Catalog Service" to "Product Service" to match documentation
- Enhanced repositories with `JpaSpecificationExecutor` for dynamic queries
- Improved pagination support with customizable sorting options

### Technical Details
- **New Classes Added:**
  - `ProductSpecification` - JPA Specifications for product filtering
  - `CategorySpecification` - JPA Specifications for category filtering

- **Enhanced Services:**
  - `ProductService.search()` - Dynamic product filtering with pagination
  - `CategoryService.search()` - Dynamic category filtering with pagination

- **New API Endpoints:**
  - `GET /api/products/search` - Advanced product search
  - `GET /api/categories/search` - Advanced category search

- **Filter Capabilities:**

  **Product Filters:**
  - `name` (String) - Case-insensitive LIKE search on product name
  - `status` (String) - Exact match on product status
  - `brand` (String) - Case-insensitive LIKE search on brand
  - `sku` (String) - Case-insensitive LIKE search on SKU
  - `categoryIds` (Set<UUID>) - Filter by one or more category IDs

  **Category Filters:**
  - `name` (String) - Case-insensitive LIKE search on category name
  - `slug` (String) - Case-insensitive LIKE search on slug
  - `parentId` (UUID) - Filter by parent category ID
  - `isRoot` (Boolean) - Filter root categories or subcategories

- **Pagination & Sorting:**
  - All search endpoints support Spring Data pagination
  - Default page size: 20 items
  - Default sorting: by name (ascending)
  - Customizable sorting with multiple sort parameters

## [0.0.1-SNAPSHOT] - 2025-08-09

### Added
- Initial project setup with Spring Boot 3.5.4
- Product and Category CRUD operations
- PostgreSQL database with Flyway migrations
- Docker Compose setup for local development
- JPA entities with proper relationships
- REST API endpoints with validation
- Global exception handling
- Pagination support for list endpoints
- Health checks with Spring Boot Actuator
- Feature-based package organization
- Testcontainers for integration testing

### Technical Stack
- **Framework**: Spring Boot 3.5.4
- **Java**: 24 (target: 17+)
- **Database**: PostgreSQL 16
- **Migration**: Flyway
- **Testing**: JUnit 5, Testcontainers
- **Build**: Maven
- **Containerization**: Docker Compose

### Database Schema
- **categories** table with hierarchical structure
- **products** table with comprehensive attributes
- **product_categories** junction table for many-to-many relationships
- UUID primary keys with PostgreSQL pgcrypto extension
- Proper indexes and constraints
- Audit timestamps (created_at, updated_at)

### API Endpoints
- Product CRUD: `/api/products`
- Category CRUD: `/api/categories`
- Pagination support with configurable page size and sorting
- Comprehensive error handling with proper HTTP status codes
- Input validation with detailed error messages

### Development Features
- Multiple Spring profiles (dev, test, prod)
- Environment-specific configuration
- VS Code launch configurations
- Docker Compose with PostgreSQL and Adminer
- Automatic database schema creation and seeding
- Health check endpoints for monitoring
