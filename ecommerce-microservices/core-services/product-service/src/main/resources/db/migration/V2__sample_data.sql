-- Flyway V2: Sample data for catalog service
-- Insert sample categories and products for development and testing

-- Insert root categories
INSERT INTO categories (id, name, slug, parent_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440001'::uuid, 'Electronics', 'electronics', NULL),
    ('550e8400-e29b-41d4-a716-446655440002'::uuid, 'Fashion', 'fashion', NULL),
    ('550e8400-e29b-41d4-a716-446655440003'::uuid, 'Home & Garden', 'home-garden', NULL),
    ('550e8400-e29b-41d4-a716-446655440004'::uuid, 'Sports & Outdoors', 'sports-outdoors', NULL),
    ('550e8400-e29b-41d4-a716-446655440005'::uuid, 'Books', 'books', NULL);

-- Insert subcategories for Electronics
INSERT INTO categories (id, name, slug, parent_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440010'::uuid, 'Smartphones', 'smartphones', '550e8400-e29b-41d4-a716-446655440001'::uuid),
    ('550e8400-e29b-41d4-a716-446655440011'::uuid, 'Laptops', 'laptops', '550e8400-e29b-41d4-a716-446655440001'::uuid),
    ('550e8400-e29b-41d4-a716-446655440012'::uuid, 'Tablets', 'tablets', '550e8400-e29b-41d4-a716-446655440001'::uuid),
    ('550e8400-e29b-41d4-a716-446655440013'::uuid, 'Audio & Headphones', 'audio-headphones', '550e8400-e29b-41d4-a716-446655440001'::uuid),
    ('550e8400-e29b-41d4-a716-446655440014'::uuid, 'Gaming', 'gaming', '550e8400-e29b-41d4-a716-446655440001'::uuid);

-- Insert subcategories for Fashion
INSERT INTO categories (id, name, slug, parent_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440020'::uuid, 'Men''s Clothing', 'mens-clothing', '550e8400-e29b-41d4-a716-446655440002'::uuid),
    ('550e8400-e29b-41d4-a716-446655440021'::uuid, 'Women''s Clothing', 'womens-clothing', '550e8400-e29b-41d4-a716-446655440002'::uuid),
    ('550e8400-e29b-41d4-a716-446655440022'::uuid, 'Shoes', 'shoes', '550e8400-e29b-41d4-a716-446655440002'::uuid),
    ('550e8400-e29b-41d4-a716-446655440023'::uuid, 'Accessories', 'accessories', '550e8400-e29b-41d4-a716-446655440002'::uuid);

-- Insert subcategories for Home & Garden
INSERT INTO categories (id, name, slug, parent_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440030'::uuid, 'Furniture', 'furniture', '550e8400-e29b-41d4-a716-446655440003'::uuid),
    ('550e8400-e29b-41d4-a716-446655440031'::uuid, 'Kitchen & Dining', 'kitchen-dining', '550e8400-e29b-41d4-a716-446655440003'::uuid),
    ('550e8400-e29b-41d4-a716-446655440032'::uuid, 'Garden Tools', 'garden-tools', '550e8400-e29b-41d4-a716-446655440003'::uuid),
    ('550e8400-e29b-41d4-a716-446655440033'::uuid, 'Home Decor', 'home-decor', '550e8400-e29b-41d4-a716-446655440003'::uuid);

-- Insert subcategories for Sports & Outdoors
INSERT INTO categories (id, name, slug, parent_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440040'::uuid, 'Fitness Equipment', 'fitness-equipment', '550e8400-e29b-41d4-a716-446655440004'::uuid),
    ('550e8400-e29b-41d4-a716-446655440041'::uuid, 'Outdoor Recreation', 'outdoor-recreation', '550e8400-e29b-41d4-a716-446655440004'::uuid),
    ('550e8400-e29b-41d4-a716-446655440042'::uuid, 'Team Sports', 'team-sports', '550e8400-e29b-41d4-a716-446655440004'::uuid);

-- Insert subcategories for Books
INSERT INTO categories (id, name, slug, parent_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440050'::uuid, 'Fiction', 'fiction', '550e8400-e29b-41d4-a716-446655440005'::uuid),
    ('550e8400-e29b-41d4-a716-446655440051'::uuid, 'Non-Fiction', 'non-fiction', '550e8400-e29b-41d4-a716-446655440005'::uuid),
    ('550e8400-e29b-41d4-a716-446655440052'::uuid, 'Technology', 'technology', '550e8400-e29b-41d4-a716-446655440005'::uuid),
    ('550e8400-e29b-41d4-a716-446655440053'::uuid, 'Children''s Books', 'childrens-books', '550e8400-e29b-41d4-a716-446655440005'::uuid);

-- Insert sample products

-- Electronics Products
INSERT INTO products (id, sku, name, slug, description, brand, status) VALUES
    ('660e8400-e29b-41d4-a716-446655440001'::uuid, 'IP15-128GB-BK', 'iPhone 15 128GB Black', 'iphone-15-128gb-black', 'Latest iPhone with advanced features and sleek design', 'Apple', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440002'::uuid, 'GS24-256GB-SL', 'Galaxy S24 256GB Silver', 'galaxy-s24-256gb-silver', 'Samsung flagship smartphone with incredible camera quality', 'Samsung', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440003'::uuid, 'MBP16-M3-512', 'MacBook Pro 16" M3 512GB', 'macbook-pro-16-m3-512gb', 'Powerful laptop for professionals and creatives', 'Apple', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440004'::uuid, 'XPS13-I7-1TB', 'Dell XPS 13 Intel i7 1TB', 'dell-xps-13-i7-1tb', 'Ultrabook with premium build and performance', 'Dell', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440005'::uuid, 'IPAD-AIR-256', 'iPad Air 256GB WiFi', 'ipad-air-256gb-wifi', 'Versatile tablet for work and entertainment', 'Apple', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440006'::uuid, 'TAB-S9-128', 'Galaxy Tab S9 128GB', 'galaxy-tab-s9-128gb', 'Android tablet with S Pen included', 'Samsung', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440007'::uuid, 'AIRPODS-PRO2', 'AirPods Pro 2nd Generation', 'airpods-pro-2nd-generation', 'Premium wireless earbuds with noise cancellation', 'Apple', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440008'::uuid, 'SONY-WH1000XM5', 'Sony WH-1000XM5 Wireless Headphones', 'sony-wh1000xm5-wireless', 'Industry leading noise canceling headphones', 'Sony', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440009'::uuid, 'PS5-STANDARD', 'PlayStation 5 Console', 'playstation-5-console', 'Next-gen gaming console with 4K gaming', 'Sony', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440010'::uuid, 'XBOX-SERIES-X', 'Xbox Series X Console', 'xbox-series-x-console', 'Most powerful Xbox console ever made', 'Microsoft', 'ACTIVE');

-- Fashion Products
INSERT INTO products (id, sku, name, slug, description, brand, status) VALUES
    ('660e8400-e29b-41d4-a716-446655440011'::uuid, 'LEV-501-32-30', 'Levi''s 501 Jeans 32x30', 'levis-501-jeans-32x30', 'Classic straight leg jeans in original blue', 'Levi''s', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440012'::uuid, 'NIKE-AIR-MAX-9', 'Nike Air Max 270 Size 9', 'nike-air-max-270-size-9', 'Comfortable running shoes with max air cushioning', 'Nike', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440013'::uuid, 'POLO-SHIRT-M', 'Ralph Lauren Polo Shirt Medium', 'ralph-lauren-polo-medium', 'Classic polo shirt in navy blue', 'Ralph Lauren', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440014'::uuid, 'ZARA-DRESS-S', 'Zara Summer Dress Small', 'zara-summer-dress-small', 'Elegant summer dress perfect for any occasion', 'Zara', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440015'::uuid, 'RAY-AVIATOR', 'Ray-Ban Aviator Sunglasses', 'rayban-aviator-sunglasses', 'Classic aviator sunglasses with UV protection', 'Ray-Ban', 'ACTIVE');

-- Home & Garden Products
INSERT INTO products (id, sku, name, slug, description, brand, status) VALUES
    ('660e8400-e29b-41d4-a716-446655440016'::uuid, 'IKEA-CHAIR-BK', 'IKEA Office Chair Black', 'ikea-office-chair-black', 'Ergonomic office chair with lumbar support', 'IKEA', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440017'::uuid, 'NINJA-BLENDER', 'Ninja Professional Blender', 'ninja-professional-blender', 'High-power blender for smoothies and food prep', 'Ninja', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440018'::uuid, 'DEWALT-DRILL', 'DeWalt Cordless Drill Kit', 'dewalt-cordless-drill-kit', 'Professional grade cordless drill with battery', 'DeWalt', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440019'::uuid, 'PLANT-MONSTERA', 'Monstera Deliciosa Plant', 'monstera-deliciosa-plant', 'Popular indoor plant for home decoration', 'GreenThumb', 'ACTIVE');

-- Sports & Outdoors Products
INSERT INTO products (id, sku, name, slug, description, brand, status) VALUES
    ('660e8400-e29b-41d4-a716-446655440020'::uuid, 'YOGA-MAT-6MM', 'Premium Yoga Mat 6mm', 'premium-yoga-mat-6mm', 'Non-slip yoga mat for all types of exercise', 'Manduka', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440021'::uuid, 'TENT-4PERSON', 'Coleman 4-Person Tent', 'coleman-4person-tent', 'Waterproof camping tent for family adventures', 'Coleman', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440022'::uuid, 'BASKETBALL-WILSON', 'Wilson Basketball Official Size', 'wilson-basketball-official', 'Official size basketball for indoor and outdoor play', 'Wilson', 'ACTIVE');

-- Books Products
INSERT INTO products (id, sku, name, slug, description, brand, status) VALUES
    ('660e8400-e29b-41d4-a716-446655440023'::uuid, 'BOOK-DUNE-HC', 'Dune by Frank Herbert Hardcover', 'dune-frank-herbert-hardcover', 'Classic science fiction novel, hardcover edition', 'Penguin Random House', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440024'::uuid, 'BOOK-CLEAN-CODE', 'Clean Code by Robert Martin', 'clean-code-robert-martin', 'Essential guide for software developers', 'Prentice Hall', 'ACTIVE'),
    ('660e8400-e29b-41d4-a716-446655440025'::uuid, 'BOOK-KIDLIT-CAT', 'The Cat in the Hat by Dr. Seuss', 'cat-in-hat-dr-seuss', 'Beloved children''s book classic', 'Random House', 'ACTIVE');

-- Product-Category associations

-- Electronics associations
INSERT INTO product_categories (product_id, category_id) VALUES
    -- iPhone 15
    ('660e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440010'::uuid), -- Smartphones
    -- Galaxy S24
    ('660e8400-e29b-41d4-a716-446655440002'::uuid, '550e8400-e29b-41d4-a716-446655440010'::uuid), -- Smartphones
    -- MacBook Pro
    ('660e8400-e29b-41d4-a716-446655440003'::uuid, '550e8400-e29b-41d4-a716-446655440011'::uuid), -- Laptops
    -- Dell XPS
    ('660e8400-e29b-41d4-a716-446655440004'::uuid, '550e8400-e29b-41d4-a716-446655440011'::uuid), -- Laptops
    -- iPad Air
    ('660e8400-e29b-41d4-a716-446655440005'::uuid, '550e8400-e29b-41d4-a716-446655440012'::uuid), -- Tablets
    -- Galaxy Tab
    ('660e8400-e29b-41d4-a716-446655440006'::uuid, '550e8400-e29b-41d4-a716-446655440012'::uuid), -- Tablets
    -- AirPods Pro
    ('660e8400-e29b-41d4-a716-446655440007'::uuid, '550e8400-e29b-41d4-a716-446655440013'::uuid), -- Audio & Headphones
    -- Sony Headphones
    ('660e8400-e29b-41d4-a716-446655440008'::uuid, '550e8400-e29b-41d4-a716-446655440013'::uuid), -- Audio & Headphones
    -- PS5
    ('660e8400-e29b-41d4-a716-446655440009'::uuid, '550e8400-e29b-41d4-a716-446655440014'::uuid), -- Gaming
    -- Xbox Series X
    ('660e8400-e29b-41d4-a716-446655440010'::uuid, '550e8400-e29b-41d4-a716-446655440014'::uuid); -- Gaming

-- Fashion associations
INSERT INTO product_categories (product_id, category_id) VALUES
    -- Levi's Jeans
    ('660e8400-e29b-41d4-a716-446655440011'::uuid, '550e8400-e29b-41d4-a716-446655440020'::uuid), -- Men's Clothing
    -- Nike Air Max
    ('660e8400-e29b-41d4-a716-446655440012'::uuid, '550e8400-e29b-41d4-a716-446655440022'::uuid), -- Shoes
    -- Polo Shirt
    ('660e8400-e29b-41d4-a716-446655440013'::uuid, '550e8400-e29b-41d4-a716-446655440020'::uuid), -- Men's Clothing
    -- Zara Dress
    ('660e8400-e29b-41d4-a716-446655440014'::uuid, '550e8400-e29b-41d4-a716-446655440021'::uuid), -- Women's Clothing
    -- Ray-Ban Sunglasses
    ('660e8400-e29b-41d4-a716-446655440015'::uuid, '550e8400-e29b-41d4-a716-446655440023'::uuid); -- Accessories

-- Home & Garden associations
INSERT INTO product_categories (product_id, category_id) VALUES
    -- IKEA Chair
    ('660e8400-e29b-41d4-a716-446655440016'::uuid, '550e8400-e29b-41d4-a716-446655440030'::uuid), -- Furniture
    -- Ninja Blender
    ('660e8400-e29b-41d4-a716-446655440017'::uuid, '550e8400-e29b-41d4-a716-446655440031'::uuid), -- Kitchen & Dining
    -- DeWalt Drill
    ('660e8400-e29b-41d4-a716-446655440018'::uuid, '550e8400-e29b-41d4-a716-446655440032'::uuid), -- Garden Tools
    -- Monstera Plant
    ('660e8400-e29b-41d4-a716-446655440019'::uuid, '550e8400-e29b-41d4-a716-446655440033'::uuid); -- Home Decor

-- Sports & Outdoors associations
INSERT INTO product_categories (product_id, category_id) VALUES
    -- Yoga Mat
    ('660e8400-e29b-41d4-a716-446655440020'::uuid, '550e8400-e29b-41d4-a716-446655440040'::uuid), -- Fitness Equipment
    -- Tent
    ('660e8400-e29b-41d4-a716-446655440021'::uuid, '550e8400-e29b-41d4-a716-446655440041'::uuid), -- Outdoor Recreation
    -- Basketball
    ('660e8400-e29b-41d4-a716-446655440022'::uuid, '550e8400-e29b-41d4-a716-446655440042'::uuid); -- Team Sports

-- Books associations
INSERT INTO product_categories (product_id, category_id) VALUES
    -- Dune
    ('660e8400-e29b-41d4-a716-446655440023'::uuid, '550e8400-e29b-41d4-a716-446655440050'::uuid), -- Fiction
    -- Clean Code
    ('660e8400-e29b-41d4-a716-446655440024'::uuid, '550e8400-e29b-41d4-a716-446655440052'::uuid), -- Technology
    -- Cat in the Hat
    ('660e8400-e29b-41d4-a716-446655440025'::uuid, '550e8400-e29b-41d4-a716-446655440053'::uuid); -- Children's Books
