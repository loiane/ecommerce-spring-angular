package com.loiane.product.category;

import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.CategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryMapper Unit Tests")
class CategoryMapperTest {

    @Test
    @DisplayName("Should map CategoryRequest to Category")
    void shouldMapCategoryRequestToCategory() {
        // Given
        var request = new CategoryRequest("Electronics", "electronics", null);

        // When
        var result = CategoryMapper.toEntity(request);

        // Then
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        assertEquals("electronics", result.getSlug());
        assertNull(result.getId()); // ID should be null for new entities
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map CategoryRequest with parent to Category")
    void shouldMapCategoryRequestWithParentToCategory() {
        // Given
        var parentId = UUID.randomUUID();
        var request = new CategoryRequest("Smartphones", "smartphones", parentId);

        // When
        var result = CategoryMapper.toEntity(request);

        // Then
        assertNotNull(result);
        assertEquals("Smartphones", result.getName());
        assertEquals("smartphones", result.getSlug());
        // Note: The mapper doesn't set parentId directly, it would need parent lookup
    }

    @Test
    @DisplayName("Should map Category to CategoryResponse")
    void shouldMapCategoryToCategoryResponse() {
        // Given
        var categoryId = UUID.randomUUID();
        var createdAt = OffsetDateTime.now().minusDays(1);
        var updatedAt = OffsetDateTime.now();

        // Use reflection to create Category with all fields set
        var category = createCategoryWithReflection(
            categoryId, "Electronics", "electronics", null, createdAt, updatedAt
        );

        // When
        var result = CategoryMapper.toResponse(category);

        // Then
        assertNotNull(result);
        assertEquals(categoryId, result.id());
        assertEquals("Electronics", result.name());
        assertEquals("electronics", result.slug());
        assertNull(result.parent());
        assertEquals(createdAt, result.createdAt());
        assertEquals(updatedAt, result.updatedAt());
    }

    @Test
    @DisplayName("Should map Category with parent to CategoryResponse")
    void shouldMapCategoryWithParentToCategoryResponse() {
        // Given
        var categoryId = UUID.randomUUID();
        var parentId = UUID.randomUUID();
        var createdAt = OffsetDateTime.now().minusDays(1);
        var updatedAt = OffsetDateTime.now();

        var parentCategory = createCategoryWithReflection(
            parentId, "Electronics", "electronics", null, createdAt, updatedAt
        );
        var category = createCategoryWithReflection(
            categoryId, "Smartphones", "smartphones", null, createdAt, updatedAt
        );
        category.setParent(parentCategory);

        // When
        var result = CategoryMapper.toResponse(category);

        // Then
        assertNotNull(result);
        assertEquals(categoryId, result.id());
        assertEquals("Smartphones", result.name());
        assertEquals("smartphones", result.slug());
        assertNotNull(result.parent());
        assertEquals(parentId, result.parent().id());
        assertEquals("Electronics", result.parent().name());
        assertEquals("electronics", result.parent().slug());
        assertEquals(createdAt, result.createdAt());
        assertEquals(updatedAt, result.updatedAt());
    }

    @Test
    @DisplayName("Should handle null CategoryRequest")
    void shouldHandleNullCategoryRequest() {
        // When
        var result = CategoryMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null Category")
    void shouldHandleNullCategory() {
        // When
        var result = CategoryMapper.toResponse(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should update existing Category from CategoryRequest")
    void shouldUpdateExistingCategoryFromCategoryRequest() {
        // Given
        var categoryId = UUID.randomUUID();
        var createdAt = OffsetDateTime.now().minusDays(1);
        var originalUpdatedAt = OffsetDateTime.now().minusHours(1);

        var existingCategory = createCategoryWithReflection(
            categoryId, "Old Name", "old-slug", null, createdAt, originalUpdatedAt
        );

        var updateRequest = new CategoryRequest("New Name", "new-slug", UUID.randomUUID());

        // When
        CategoryMapper.updateEntity(existingCategory, updateRequest);

        // Then
        assertEquals("New Name", existingCategory.getName());
        assertEquals("new-slug", existingCategory.getSlug());
        assertEquals(categoryId, existingCategory.getId()); // ID should not change
        assertEquals(createdAt, existingCategory.getCreatedAt()); // CreatedAt should not change
    }

    @Test
    @DisplayName("Should handle null parameters in updateEntity")
    void shouldHandleNullParametersInUpdateEntity() {
        // Given
        var category = new Category("Test", "test");

        // When & Then - should not throw exceptions
        assertDoesNotThrow(() -> CategoryMapper.updateEntity(null, null));
        assertDoesNotThrow(() -> CategoryMapper.updateEntity(category, null));
        assertDoesNotThrow(() -> CategoryMapper.updateEntity(null, new CategoryRequest("Test", "test", null)));
    }

    @Test
    @DisplayName("Should convert list of categories to response list")
    void shouldConvertListOfCategoriesToResponseList() {
        // Given
        var category1 = createCategoryWithReflection(
            UUID.randomUUID(), "Category 1", "category-1", null,
            OffsetDateTime.now(), OffsetDateTime.now()
        );
        var category2 = createCategoryWithReflection(
            UUID.randomUUID(), "Category 2", "category-2", null,
            OffsetDateTime.now(), OffsetDateTime.now()
        );
        var categories = List.of(category1, category2);

        // When
        var result = CategoryMapper.toResponseList(categories);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).name());
        assertEquals("Category 2", result.get(1).name());
    }

    @Test
    @DisplayName("Should handle empty list in toResponseList")
    void shouldHandleEmptyListInToResponseList() {
        // When
        var result = CategoryMapper.toResponseList(List.of());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null list in toResponseList")
    void shouldHandleNullListInToResponseList() {
        // When
        var result = CategoryMapper.toResponseList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Helper method to create Category using reflection since the entity has no setters for ID
     */
    private Category createCategoryWithReflection(UUID id, String name, String slug, UUID parentId,
                                                  OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        try {
            var category = new Category(name, slug);

            // Set ID using reflection
            var idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, id);

            // Set parent using reflection if parentId is not null
            if (parentId != null) {
                var parentField = Category.class.getDeclaredField("parent");
                parentField.setAccessible(true);
                var parent = new Category("Parent", "parent");
                var parentIdField = Category.class.getDeclaredField("id");
                parentIdField.setAccessible(true);
                parentIdField.set(parent, parentId);
                parentField.set(category, parent);
            }

            // Set createdAt using reflection
            var createdAtField = Category.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(category, createdAt);

            // Set updatedAt using reflection
            var updatedAtField = Category.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(category, updatedAt);

            return category;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Category with reflection", e);
        }
    }
}
