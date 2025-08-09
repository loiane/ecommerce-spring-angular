package com.loiane.product.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("CategorySpecification Unit Tests")
class CategorySpecificationTest {

    @ParameterizedTest
    @DisplayName("Should return non-null specification for various name inputs")
    @ValueSource(strings = {"Electronics", "Electronics & Gadgets", "Electrónicos"})
    @NullAndEmptySource
    void shouldReturnNonNullSpecificationForVariousNames(String name) {
        // When
        Specification<Category> spec = CategorySpecification.hasName(name);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should handle very long names")
    void shouldHandleVeryLongNames() {
        // Given
        String longName = "A".repeat(1000);

        // When
        Specification<Category> spec = CategorySpecification.hasName(longName);

        // Then
        assertNotNull(spec);
    }

    @ParameterizedTest
    @DisplayName("Should return non-null specification for various slug inputs")
    @ValueSource(strings = {"electronics", "electronics-2024"})
    @NullAndEmptySource
    void shouldReturnNonNullSpecificationForVariousSlugs(String slug) {
        // When
        Specification<Category> spec = CategorySpecification.hasSlug(slug);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should handle very long slugs")
    void shouldHandleVeryLongSlugs() {
        // Given
        String longSlug = "a".repeat(500);

        // When
        Specification<Category> spec = CategorySpecification.hasSlug(longSlug);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for valid parent ID")
    void shouldReturnNonNullSpecificationForValidParentId() {
        // Given
        UUID parentId = UUID.randomUUID();

        // When
        Specification<Category> spec = CategorySpecification.hasParent(parentId);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for null parent ID")
    void shouldReturnNonNullSpecificationForNullParentId() {
        // When
        Specification<Category> spec = CategorySpecification.hasParent(null);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for root category check")
    void shouldReturnNonNullSpecificationForRootCategoryCheck() {
        // When
        Specification<Category> spec = CategorySpecification.isRootCategory();

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for sub-categories check")
    void shouldReturnNonNullSpecificationForSubCategoriesCheck() {
        // When
        Specification<Category> spec = CategorySpecification.hasSubCategories();

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should compose specifications with AND")
    void shouldComposeSpecificationsWithAnd() {
        // Given
        Specification<Category> nameSpec = CategorySpecification.hasName("Electronics");
        Specification<Category> slugSpec = CategorySpecification.hasSlug("electronics");

        // When
        Specification<Category> combinedSpec = nameSpec.and(slugSpec);

        // Then
        assertNotNull(combinedSpec);
    }

    @Test
    @DisplayName("Should compose specifications with OR")
    void shouldComposeSpecificationsWithOr() {
        // Given
        Specification<Category> nameSpec = CategorySpecification.hasName("Electronics");
        Specification<Category> rootSpec = CategorySpecification.isRootCategory();

        // When
        Specification<Category> combinedSpec = nameSpec.or(rootSpec);

        // Then
        assertNotNull(combinedSpec);
    }

    @Test
    @DisplayName("Should negate specifications")
    void shouldNegateSpecifications() {
        // Given
        Specification<Category> rootSpec = CategorySpecification.isRootCategory();

        // When
        Specification<Category> negatedSpec = Specification.not(rootSpec);

        // Then
        assertNotNull(negatedSpec);
    }

    @Test
    @DisplayName("Should compose complex specifications")
    void shouldComposeComplexSpecifications() {
        // Given
        UUID parentId = UUID.randomUUID();

        // When
        Specification<Category> complexSpec = CategorySpecification.hasName("Electronics")
            .and(CategorySpecification.hasSlug("electronics"))
            .and(CategorySpecification.hasParent(parentId))
            .and(CategorySpecification.hasSubCategories());

        // Then
        assertNotNull(complexSpec);
    }

    @Test
    @DisplayName("Should combine parent and root category specifications")
    void shouldCombineParentAndRootCategorySpecifications() {
        // Given
        UUID parentId = UUID.randomUUID();

        // When
        Specification<Category> spec = CategorySpecification.hasParent(parentId)
            .or(CategorySpecification.isRootCategory());

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should combine name search with hierarchy specifications")
    void shouldCombineNameSearchWithHierarchySpecifications() {
        // When
        Specification<Category> spec = CategorySpecification.hasName("Electronics")
            .and(CategorySpecification.hasSubCategories())
            .and(Specification.not(CategorySpecification.isRootCategory()));

        // Then
        assertNotNull(spec);
    }
}
