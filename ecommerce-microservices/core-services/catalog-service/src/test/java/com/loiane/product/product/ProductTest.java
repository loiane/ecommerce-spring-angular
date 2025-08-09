package com.loiane.product.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Entity Unit Tests")
class ProductTest {

    @Test
    @DisplayName("Should create Product with required fields")
    void shouldCreateProductWithRequiredFields() {
        // When
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // Then
        assertNotNull(product);
        assertEquals("IP15-128GB", product.getSku());
        assertEquals("iPhone 15", product.getName());
        assertEquals("iphone-15", product.getSlug());
        assertNull(product.getId()); // ID should be null until persisted
        assertNull(product.getBrand());
        assertNull(product.getDescription());
        assertEquals("ACTIVE", product.getStatus()); // Status has default value
        assertNull(product.getCreatedAt());
        assertNull(product.getUpdatedAt());
        assertNotNull(product.getCategories()); // Categories is initialized as empty set
        assertTrue(product.getCategories().isEmpty());
    }

    @Test
    @DisplayName("Should not allow null SKU")
    void shouldNotAllowNullSku() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
            new Product(null, "iPhone 15", "iphone-15"));
    }

    @Test
    @DisplayName("Should not allow null name")
    void shouldNotAllowNullName() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
            new Product("IP15-128GB", null, "iphone-15"));
    }

    @Test
    @DisplayName("Should not allow null slug")
    void shouldNotAllowNullSlug() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
            new Product("IP15-128GB", "iPhone 15", null));
    }

    @Test
    @DisplayName("Should set and get brand")
    void shouldSetAndGetBrand() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When
        product.setBrand("Apple");

        // Then
        assertEquals("Apple", product.getBrand());
    }

    @Test
    @DisplayName("Should set and get description")
    void shouldSetAndGetDescription() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When
        product.setDescription("Latest iPhone model");

        // Then
        assertEquals("Latest iPhone model", product.getDescription());
    }

    @Test
    @DisplayName("Should set and get status")
    void shouldSetAndGetStatus() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When
        product.setStatus("ACTIVE");

        // Then
        assertEquals("ACTIVE", product.getStatus());
    }

    @Test
    @DisplayName("Should not allow null SKU in setter")
    void shouldNotAllowNullSkuInSetter() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertThrows(NullPointerException.class, () -> product.setSku(null));
    }

    @Test
    @DisplayName("Should not allow null name in setter")
    void shouldNotAllowNullNameInSetter() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertThrows(NullPointerException.class, () -> product.setName(null));
    }

    @Test
    @DisplayName("Should not allow null slug in setter")
    void shouldNotAllowNullSlugInSetter() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertThrows(NullPointerException.class, () -> product.setSlug(null));
    }

    @Test
    @DisplayName("Should update SKU")
    void shouldUpdateSku() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When
        product.setSku("IP15-256GB");

        // Then
        assertEquals("IP15-256GB", product.getSku());
    }

    @Test
    @DisplayName("Should update name")
    void shouldUpdateName() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When
        product.setName("iPhone 15 Pro");

        // Then
        assertEquals("iPhone 15 Pro", product.getName());
    }

    @Test
    @DisplayName("Should update slug")
    void shouldUpdateSlug() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When
        product.setSlug("iphone-15-pro");

        // Then
        assertEquals("iphone-15-pro", product.getSlug());
    }

    @Test
    @DisplayName("Should get categories")
    void shouldGetCategories() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertNotNull(product.getCategories()); // Categories are initialized as empty set
        assertTrue(product.getCategories().isEmpty()); // Should be empty initially
    }

    @Test
    @DisplayName("Should test equals with same ID")
    void shouldTestEqualsWithSameId() {
        // Given
        var product1 = createProductWithId(UUID.randomUUID());
        var product2 = createProductWithId(product1.getId());

        // When & Then
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("Should test equals with different ID")
    void shouldTestEqualsWithDifferentId() {
        // Given
        var product1 = createProductWithId(UUID.randomUUID());
        var product2 = createProductWithId(UUID.randomUUID());

        // When & Then
        assertNotEquals(product1, product2);
    }

    @Test
    @DisplayName("Should test equals with null ID")
    void shouldTestEqualsWithNullId() {
        // Given
        var product1 = new Product("SKU1", "Product 1", "product-1");
        var product2 = new Product("SKU2", "Product 2", "product-2");

        // When & Then
        assertNotEquals(product1, product2);
    }

    @Test
    @DisplayName("Should test equals with same object")
    void shouldTestEqualsWithSameObject() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertEquals(product, product);
    }

    @Test
    @DisplayName("Should test equals with null")
    void shouldTestEqualsWithNull() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertNotEquals(null, product);
    }

    @Test
    @DisplayName("Should test equals with different class")
    void shouldTestEqualsWithDifferentClass() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertNotEquals("not a product", product);
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");

        // When & Then
        assertEquals(31, product.hashCode());
    }

    @Test
    @DisplayName("Should simulate prePersist behavior")
    void shouldSimulatePrePersistBehavior() {
        // Given
        var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");
        var beforePersist = OffsetDateTime.now();

        // When - simulate what @PrePersist would do
        try {
            var prePersistMethod = Product.class.getDeclaredMethod("prePersist");
            prePersistMethod.setAccessible(true);
            prePersistMethod.invoke(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke prePersist", e);
        }

        // Then
        var afterPersist = OffsetDateTime.now();
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
        assertTrue(product.getCreatedAt().isAfter(beforePersist.minusSeconds(1)));
        assertTrue(product.getCreatedAt().isBefore(afterPersist.plusSeconds(1)));
        assertEquals(product.getCreatedAt(), product.getUpdatedAt());
    }

    @Test
    @DisplayName("Should simulate preUpdate behavior")
    void shouldSimulatePreUpdateBehavior() {
        // Given
        var product = createProductWithTimestamps();
        var originalCreatedAt = product.getCreatedAt();
        var beforeUpdate = OffsetDateTime.now();

        // When - simulate what @PreUpdate would do
        try {
            var preUpdateMethod = Product.class.getDeclaredMethod("preUpdate");
            preUpdateMethod.setAccessible(true);
            preUpdateMethod.invoke(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke preUpdate", e);
        }

        // Then
        var afterUpdate = OffsetDateTime.now();
        assertEquals(originalCreatedAt, product.getCreatedAt()); // CreatedAt should not change
        assertNotNull(product.getUpdatedAt());
        assertTrue(product.getUpdatedAt().isAfter(beforeUpdate.minusSeconds(1)));
        assertTrue(product.getUpdatedAt().isBefore(afterUpdate.plusSeconds(1)));
    }

    /**
     * Helper method to create Product with ID using reflection
     */
    private Product createProductWithId(UUID id) {
        try {
            var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");
            var idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(product, id);
            return product;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    /**
     * Helper method to create Product with timestamps using reflection
     */
    private Product createProductWithTimestamps() {
        try {
            var product = new Product("IP15-128GB", "iPhone 15", "iphone-15");
            var now = OffsetDateTime.now().minusHours(1);

            var createdAtField = Product.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(product, now);

            var updatedAtField = Product.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(product, now);

            return product;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set timestamps", e);
        }
    }
}
