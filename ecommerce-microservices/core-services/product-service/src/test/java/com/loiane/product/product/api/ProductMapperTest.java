package com.loiane.product.product.api;

import com.loiane.product.category.Category;
import com.loiane.product.product.Product;
import com.loiane.product.product.api.dto.ProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductMapper Unit Tests")
class ProductMapperTest {

    @Test
    @DisplayName("Should map ProductRequest to Product")
    void shouldMapProductRequestToProduct() {
        // Given
        var request = new ProductRequest(
            "IP15-128GB", "iPhone 15", "iphone-15", "Apple",
            "Latest iPhone model", "ACTIVE", Set.of()
        );

        // When
        var result = ProductMapper.toEntity(request);

        // Then
        assertNotNull(result);
        assertEquals("IP15-128GB", result.getSku());
        assertEquals("iPhone 15", result.getName());
        assertEquals("iphone-15", result.getSlug());
        assertEquals("Apple", result.getBrand());
        assertEquals("Latest iPhone model", result.getDescription());
        assertEquals("ACTIVE", result.getStatus());
        assertNull(result.getId()); // ID should be null for new entities
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle null ProductRequest")
    void shouldHandleNullProductRequest() {
        // When
        var result = ProductMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should update existing Product from ProductRequest")
    void shouldUpdateExistingProductFromProductRequest() {
        // Given
        var existingProduct = createProductWithReflection(
            UUID.randomUUID(), "OLD-SKU", "Old Name", "old-slug", "Old Brand",
            "Old Description", "INACTIVE", OffsetDateTime.now(), OffsetDateTime.now()
        );

        var updateRequest = new ProductRequest(
            "NEW-SKU", "New Name", "new-slug", "New Brand",
            "New Description", "ACTIVE", Set.of()
        );

        // When
        ProductMapper.updateEntity(existingProduct, updateRequest);

        // Then
        assertEquals("NEW-SKU", existingProduct.getSku());
        assertEquals("New Name", existingProduct.getName());
        assertEquals("new-slug", existingProduct.getSlug());
        assertEquals("New Brand", existingProduct.getBrand());
        assertEquals("New Description", existingProduct.getDescription());
        assertEquals("ACTIVE", existingProduct.getStatus());
    }

    @Test
    @DisplayName("Should handle null parameters in updateEntity")
    void shouldHandleNullParametersInUpdateEntity() {
        // Given
        var product = new Product("SKU123", "Test Product", "test-product");

        // When & Then - should not throw exceptions
        assertDoesNotThrow(() -> ProductMapper.updateEntity(null, null));
        assertDoesNotThrow(() -> ProductMapper.updateEntity(product, null));
        assertDoesNotThrow(() -> ProductMapper.updateEntity(null,
            new ProductRequest("SKU", "Name", "slug", "Brand", "Desc", "ACTIVE", Set.of())));
    }

    @Test
    @DisplayName("Should map Product to ProductResponse")
    void shouldMapProductToProductResponse() {
        // Given
        var productId = UUID.randomUUID();
        var createdAt = OffsetDateTime.now().minusDays(1);
        var updatedAt = OffsetDateTime.now();

        var product = createProductWithReflection(
            productId, "IP15-128GB", "iPhone 15", "iphone-15", "Apple",
            "Latest iPhone model", "ACTIVE", createdAt, updatedAt
        );

        // When
        var result = ProductMapper.toResponse(product);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("IP15-128GB", result.sku());
        assertEquals("iPhone 15", result.name());
        assertEquals("iphone-15", result.slug());
        assertEquals("Apple", result.brand());
        assertEquals("Latest iPhone model", result.description());
        assertEquals("ACTIVE", result.status());
        assertEquals(createdAt, result.createdAt());
        assertEquals(updatedAt, result.updatedAt());
        assertNotNull(result.categories());
        assertTrue(result.categories().isEmpty());
    }

    @Test
    @DisplayName("Should map Product with categories to ProductResponse")
    void shouldMapProductWithCategoriesToProductResponse() {
        // Given
        var productId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        var createdAt = OffsetDateTime.now().minusDays(1);
        var updatedAt = OffsetDateTime.now();

        var category = createCategoryWithReflection(categoryId, "Electronics", "electronics");
        var product = createProductWithReflection(
            productId, "IP15-128GB", "iPhone 15", "iphone-15", "Apple",
            "Latest iPhone model", "ACTIVE", createdAt, updatedAt
        );

        // Set categories using reflection
        try {
            var categoriesField = Product.class.getDeclaredField("categories");
            categoriesField.setAccessible(true);
            categoriesField.set(product, Set.of(category));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set categories", e);
        }

        // When
        var result = ProductMapper.toResponse(product);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.id());
        assertNotNull(result.categories());
        assertEquals(1, result.categories().size());

        var categorySummary = result.categories().get(0);
        assertEquals(categoryId, categorySummary.id());
        assertEquals("Electronics", categorySummary.name());
        assertEquals("electronics", categorySummary.slug());
    }

    @Test
    @DisplayName("Should handle null Product in toResponse")
    void shouldHandleNullProductInToResponse() {
        // When
        var result = ProductMapper.toResponse(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should extract category IDs from ProductRequest")
    void shouldExtractCategoryIdsFromProductRequest() {
        // Given
        var categoryId1 = UUID.randomUUID();
        var categoryId2 = UUID.randomUUID();
        var request = new ProductRequest(
            "SKU", "Name", "slug", "Brand", "Desc", "ACTIVE",
            Set.of(categoryId1, categoryId2)
        );

        // When
        var result = ProductMapper.extractCategoryIds(request);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(categoryId1));
        assertTrue(result.contains(categoryId2));
    }

    @Test
    @DisplayName("Should handle null request in extractCategoryIds")
    void shouldHandleNullRequestInExtractCategoryIds() {
        // When
        var result = ProductMapper.extractCategoryIds(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null categoryIds in extractCategoryIds")
    void shouldHandleNullCategoryIdsInExtractCategoryIds() {
        // Given
        var request = new ProductRequest(
            "SKU", "Name", "slug", "Brand", "Desc", "ACTIVE", null
        );

        // When
        var result = ProductMapper.extractCategoryIds(request);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should convert list of products to response list")
    void shouldConvertListOfProductsToResponseList() {
        // Given
        var product1 = createProductWithReflection(
            UUID.randomUUID(), "SKU1", "Product 1", "product-1", "Brand1",
            "Description 1", "ACTIVE", OffsetDateTime.now(), OffsetDateTime.now()
        );
        var product2 = createProductWithReflection(
            UUID.randomUUID(), "SKU2", "Product 2", "product-2", "Brand2",
            "Description 2", "INACTIVE", OffsetDateTime.now(), OffsetDateTime.now()
        );
        var products = List.of(product1, product2);

        // When
        var result = ProductMapper.toResponseList(products);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).name());
        assertEquals("Product 2", result.get(1).name());
    }

    @Test
    @DisplayName("Should handle empty list in toResponseList")
    void shouldHandleEmptyListInToResponseList() {
        // When
        var result = ProductMapper.toResponseList(List.of());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null list in toResponseList")
    void shouldHandleNullListInToResponseList() {
        // When
        var result = ProductMapper.toResponseList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Helper method to create Product using reflection
     */
    private Product createProductWithReflection(UUID id, String sku, String name, String slug,
                                               String brand, String description, String status,
                                               OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        try {
            var product = new Product(sku, name, slug);
            product.setBrand(brand);
            product.setDescription(description);
            product.setStatus(status);

            // Set ID using reflection
            var idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(product, id);

            // Set createdAt using reflection
            var createdAtField = Product.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(product, createdAt);

            // Set updatedAt using reflection
            var updatedAtField = Product.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(product, updatedAt);

            return product;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Product with reflection", e);
        }
    }

    /**
     * Helper method to create Category using reflection
     */
    private Category createCategoryWithReflection(UUID id, String name, String slug) {
        try {
            var category = new Category(name, slug);

            // Set ID using reflection
            var idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, id);

            return category;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Category with reflection", e);
        }
    }
}
