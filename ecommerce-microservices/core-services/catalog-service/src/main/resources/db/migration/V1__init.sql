-- Flyway V1: Initial catalog schema (hardened)
-- Tables: categories, products, product_categories (join)

-- Use pgcrypto for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(140) NOT NULL,
    parent_id UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT chk_categories_slug_format CHECK (slug ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$')
);

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    slug VARCHAR(180) NOT NULL,
    description TEXT NULL,
    brand VARCHAR(120) NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_products_slug_format CHECK (slug ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$'),
    CONSTRAINT chk_products_sku_format CHECK (sku ~ '^[A-Za-z0-9_.-]+$'),
    CONSTRAINT chk_products_status_values CHECK (status IN ('ACTIVE','INACTIVE','DRAFT'))
);

CREATE TABLE product_categories (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, category_id)
);

-- Indexes for filtering/search
CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_status ON products (status);
CREATE INDEX idx_product_categories_category ON product_categories (category_id);

-- Case-insensitive uniqueness for slugs
CREATE UNIQUE INDEX ux_categories_slug_ci ON categories (LOWER(slug));
CREATE UNIQUE INDEX ux_products_slug_ci ON products (LOWER(slug));

-- Trigger functions to auto-update updated_at
CREATE OR REPLACE FUNCTION public.set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_categories_updated
BEFORE UPDATE ON categories
FOR EACH ROW
EXECUTE FUNCTION public.set_timestamp();

CREATE TRIGGER trg_products_updated
BEFORE UPDATE ON products
FOR EACH ROW
EXECUTE FUNCTION public.set_timestamp();
