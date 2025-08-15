# Database Schema Documentation

## Overview

The Product Catalog Service uses PostgreSQL 16 as its primary database with Flyway for schema versioning and migrations. The database schema follows a normalized design with proper relationships, constraints, and indexes.

## Database Configuration

- **Database**: PostgreSQL 16
- **Schema**: `public` (default)
- **Migration Tool**: Flyway
- **Connection Pool**: HikariCP
- **Extensions**: `pgcrypto` for UUID generation

## Tables

### 1. Categories Table

**Purpose**: Stores product categories with hierarchical parent-child relationships.

```sql
CREATE TABLE categories (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id UUID REFERENCES categories(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
```

**Columns:**
- `id` (UUID, PK): Unique identifier, auto-generated using PostgreSQL's `gen_random_uuid()`
- `name` (VARCHAR(255), NOT NULL): Human-readable category name
- `slug` (VARCHAR(255), UNIQUE, NOT NULL): URL-friendly identifier
- `description` (VARCHAR(500)): Optional category description
- `parent_id` (UUID, FK): Self-referencing foreign key for hierarchical structure
- `created_at` (TIMESTAMPTZ, NOT NULL): Record creation timestamp
- `updated_at` (TIMESTAMPTZ, NOT NULL): Last modification timestamp

**Constraints:**
- PRIMARY KEY on `id`
- UNIQUE constraint on `slug` (case-insensitive)
- FOREIGN KEY `parent_id` references `categories(id)` with CASCADE delete
- CHECK constraint: `name` length > 0
- CHECK constraint: `slug` length > 0

**Indexes:**
- Primary key index on `id`
- Unique index on `slug` (case-insensitive)
- Index on `parent_id` for hierarchy queries
- Index on `name` for search performance

### 2. Products Table

**Purpose**: Stores product information with attributes, pricing, and status.

```sql
CREATE TABLE products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    sku VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
```

**Columns:**
- `id` (UUID, PK): Unique identifier, auto-generated using PostgreSQL's `gen_random_uuid()`
- `name` (VARCHAR(255), NOT NULL): Product name
- `description` (TEXT): Detailed product description
- `sku` (VARCHAR(100), UNIQUE, NOT NULL): Stock Keeping Unit, unique across all products
- `price` (DECIMAL(10,2), NOT NULL): Product price with 2 decimal places
- `brand` (VARCHAR(100), NOT NULL): Product brand/manufacturer
- `status` (VARCHAR(20), NOT NULL): Product status (ACTIVE, DRAFT, DISCONTINUED)
- `created_at` (TIMESTAMPTZ, NOT NULL): Record creation timestamp
- `updated_at` (TIMESTAMPTZ, NOT NULL): Last modification timestamp

**Constraints:**
- PRIMARY KEY on `id`
- UNIQUE constraint on `sku` (case-insensitive)
- CHECK constraint: `name` length > 0
- CHECK constraint: `sku` length > 0
- CHECK constraint: `price` > 0
- CHECK constraint: `brand` length > 0
- CHECK constraint: `status` IN ('ACTIVE', 'DRAFT', 'DISCONTINUED')

**Indexes:**
- Primary key index on `id`
- Unique index on `sku` (case-insensitive)
- Index on `name` for search performance
- Index on `brand` for filtering
- Index on `status` for filtering
- Composite index on `(status, name)` for common queries

### 3. Product_Categories Table (Junction Table)

**Purpose**: Many-to-many relationship between products and categories.

```sql
CREATE TABLE product_categories (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, category_id)
);
```

**Columns:**
- `product_id` (UUID, FK): Reference to products table
- `category_id` (UUID, FK): Reference to categories table

**Constraints:**
- Composite PRIMARY KEY on `(product_id, category_id)`
- FOREIGN KEY `product_id` references `products(id)` with CASCADE delete
- FOREIGN KEY `category_id` references `categories(id)` with CASCADE delete

**Indexes:**
- Primary key index on `(product_id, category_id)`
- Index on `product_id` for product → categories queries
- Index on `category_id` for category → products queries

## Relationships

### Category Hierarchy
```
categories (self-referencing)
├── parent_id → categories.id
└── Supports unlimited depth hierarchy
```

**Example Hierarchy:**
```
Electronics (parent_id: NULL)
├── Computers (parent_id: electronics_id)
│   ├── Laptops (parent_id: computers_id)
│   └── Desktops (parent_id: computers_id)
└── Mobile Phones (parent_id: electronics_id)
```

### Product-Category Association
```
products ←→ product_categories ←→ categories
        (many-to-many relationship)
```

**Example Associations:**
```
MacBook Pro 14" → [Laptops, Computers, Electronics]
iPhone 15 → [Mobile Phones, Electronics]
Gaming Mouse → [Computer Accessories, Gaming, Electronics]
```

## Database Triggers

### Automatic Timestamp Updates

```sql
-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers for automatic timestamp updates
CREATE TRIGGER update_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## Indexes for Performance

### Categories Table Indexes
```sql
-- Primary key (automatic)
CREATE UNIQUE INDEX categories_pkey ON categories(id);

-- Unique slug constraint
CREATE UNIQUE INDEX categories_slug_key ON categories(lower(slug));

-- Parent-child relationships
CREATE INDEX idx_categories_parent_id ON categories(parent_id);

-- Search performance
CREATE INDEX idx_categories_name ON categories(lower(name));
```

### Products Table Indexes
```sql
-- Primary key (automatic)
CREATE UNIQUE INDEX products_pkey ON products(id);

-- Unique SKU constraint
CREATE UNIQUE INDEX products_sku_key ON products(lower(sku));

-- Search and filtering indexes
CREATE INDEX idx_products_name ON products(lower(name));
CREATE INDEX idx_products_brand ON products(lower(brand));
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_price ON products(price);

-- Composite indexes for common queries
CREATE INDEX idx_products_status_name ON products(status, lower(name));
CREATE INDEX idx_products_brand_status ON products(lower(brand), status);
```

### Product_Categories Junction Table Indexes
```sql
-- Primary key (automatic)
CREATE UNIQUE INDEX product_categories_pkey ON product_categories(product_id, category_id);

-- Foreign key indexes for performance
CREATE INDEX idx_product_categories_product_id ON product_categories(product_id);
CREATE INDEX idx_product_categories_category_id ON product_categories(category_id);
```

## Data Types and Constraints

### UUID Generation
- Uses PostgreSQL's `pgcrypto` extension
- `gen_random_uuid()` function for cryptographically secure UUIDs
- All primary keys are UUIDs for distributed system compatibility

### Timestamp Handling
- All timestamps use `TIMESTAMP WITH TIME ZONE` for timezone awareness
- Automatic `created_at` and `updated_at` management via triggers
- UTC storage with application-level timezone conversion

### Text and Character Limits
- **Category name**: 255 characters max
- **Category slug**: 255 characters max, URL-safe
- **Category description**: 500 characters max
- **Product name**: 255 characters max
- **Product SKU**: 100 characters max, must be unique
- **Product brand**: 100 characters max
- **Product description**: TEXT (unlimited)

### Numeric Constraints
- **Product price**: DECIMAL(10,2) - supports up to $99,999,999.99
- **Price validation**: Must be positive (> 0)

### Enum-like Constraints
- **Product status**: Must be one of 'ACTIVE', 'DRAFT', 'DISCONTINUED'

## Schema Migration History

### V1__init.sql (Initial Schema)
- Created `categories` table with hierarchical structure
- Created `products` table with comprehensive attributes
- Created `product_categories` junction table
- Added all necessary constraints and indexes
- Set up automatic timestamp triggers
- Enabled `pgcrypto` extension for UUID generation

### Future Migrations (Planned)
- V2: Add product inventory tracking fields
- V3: Add product images and media support
- V4: Add product variants (size, color, etc.)
- V5: Add audit trail tables
- V6: Add full-text search indexes

## Query Examples

### Common Category Queries
```sql
-- Find all root categories
SELECT * FROM categories WHERE parent_id IS NULL;

-- Find subcategories of a parent
SELECT * FROM categories WHERE parent_id = 'parent-uuid';

-- Get category hierarchy (recursive)
WITH RECURSIVE category_tree AS (
    SELECT id, name, parent_id, 0 as level
    FROM categories WHERE parent_id IS NULL
    UNION ALL
    SELECT c.id, c.name, c.parent_id, ct.level + 1
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT * FROM category_tree ORDER BY level, name;
```

### Common Product Queries
```sql
-- Find active products by name
SELECT * FROM products
WHERE status = 'ACTIVE'
AND lower(name) LIKE '%laptop%';

-- Products in specific categories
SELECT p.* FROM products p
JOIN product_categories pc ON p.id = pc.product_id
WHERE pc.category_id IN ('cat1-uuid', 'cat2-uuid')
AND p.status = 'ACTIVE';

-- Products with category information
SELECT p.*, c.name as category_name
FROM products p
JOIN product_categories pc ON p.id = pc.product_id
JOIN categories c ON pc.category_id = c.id
WHERE p.status = 'ACTIVE'
ORDER BY p.name;
```

### Performance Query Examples
```sql
-- Efficient category product count
SELECT c.name, COUNT(pc.product_id) as product_count
FROM categories c
LEFT JOIN product_categories pc ON c.id = pc.category_id
LEFT JOIN products p ON pc.product_id = p.id AND p.status = 'ACTIVE'
GROUP BY c.id, c.name
ORDER BY product_count DESC;

-- Product search with filters (uses indexes)
SELECT p.* FROM products p
WHERE p.status = 'ACTIVE'
AND lower(p.name) LIKE '%search%'
AND lower(p.brand) LIKE '%brand%'
AND p.price BETWEEN 100.00 AND 1000.00
ORDER BY p.name
LIMIT 20 OFFSET 0;
```

## Performance Considerations

### Index Usage
- All search fields (`name`, `brand`, `status`) have indexes
- Case-insensitive searches use functional indexes with `lower()`
- Composite indexes for common filter combinations
- Foreign key indexes for join performance

### Query Optimization
- Use prepared statements to avoid SQL injection and improve performance
- Leverage connection pooling (HikariCP) for database connections
- Use pagination (`LIMIT`/`OFFSET`) for large result sets
- Consider materialized views for complex hierarchical queries

### Maintenance
- Regular `VACUUM` and `ANALYZE` operations
- Monitor index usage with `pg_stat_user_indexes`
- Consider partitioning for very large product catalogs
- Archive old data to maintain performance

## Security Considerations

### Data Protection
- No sensitive data stored in plain text
- UUID primary keys prevent enumeration attacks
- Proper foreign key constraints prevent data inconsistency

### Access Control
- Database-level user permissions (application user has limited privileges)
- Connection pooling with secure credentials
- SSL/TLS encryption for database connections in production

### Audit Trail
- Automatic timestamp tracking for all changes
- Consider adding audit tables for compliance requirements
- Log all schema changes through Flyway migrations

## Backup and Recovery

### Backup Strategy
- Regular PostgreSQL dumps using `pg_dump`
- Point-in-time recovery with WAL archiving
- Cross-region backups for disaster recovery

### Testing
- Use Testcontainers for integration testing with real PostgreSQL
- Test migrations on copy of production data
- Verify backup/restore procedures regularly
