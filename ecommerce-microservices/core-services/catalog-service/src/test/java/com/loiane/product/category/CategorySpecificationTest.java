package com.loiane.product.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("CategorySpecification Unit Tests")
class CategorySpecificationTest {

    @Nested
    @DisplayName("hasName() method tests")
    class HasNameTests {

        @Test
        @DisplayName("Should return non-null specification for valid name")
        void shouldReturnNonNullSpecificationForValidName() {
            // When
            Specification<Category> spec = CategorySpecification.hasName("Electronics");

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should return non-null specification for null name")
        void shouldReturnNonNullSpecificationForNullName() {
            // When
            Specification<Category> spec = CategorySpecification.hasName(null);

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should return non-null specification for empty name")
        void shouldReturnNonNullSpecificationForEmptyName() {
            // When
            Specification<Category> spec = CategorySpecification.hasName("   ");

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should handle name with special characters")
        void shouldHandleNameWithSpecialCharacters() {
            // When
            Specification<Category> spec = CategorySpecification.hasName("Electronics & Gadgets");

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should handle unicode category names")
        void shouldHandleUnicodeCategoryNames() {
            // When
            Specification<Category> spec = CategorySpecification.hasName("Electrónicos");

            // Then
            assertNotNull(spec);
        }
    }

    @Nested
    @DisplayName("hasSlug() method tests")
    class HasSlugTests {

        @Test
        @DisplayName("Should return non-null specification for valid slug")
        void shouldReturnNonNullSpecificationForValidSlug() {
            // When
            Specification<Category> spec = CategorySpecification.hasSlug("electronics");

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should return non-null specification for null slug")
        void shouldReturnNonNullSpecificationForNullSlug() {
            // When
            Specification<Category> spec = CategorySpecification.hasSlug(null);

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should return non-null specification for empty slug")
        void shouldReturnNonNullSpecificationForEmptySlug() {
            // When
            Specification<Category> spec = CategorySpecification.hasSlug("");

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should handle slug with dashes and numbers")
        void shouldHandleSlugWithDashesAndNumbers() {
            // When
            Specification<Category> spec = CategorySpecification.hasSlug("electronics-2024");

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
    }

    @Nested
    @DisplayName("hasParent() method tests")
    class HasParentTests {

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
        @DisplayName("Should handle multiple calls with same parent ID")
        void shouldHandleMultipleCallsWithSameParentId() {
            // Given
            UUID parentId = UUID.randomUUID();

            // When
            Specification<Category> spec1 = CategorySpecification.hasParent(parentId);
            Specification<Category> spec2 = CategorySpecification.hasParent(parentId);

            // Then
            assertNotNull(spec1);
            assertNotNull(spec2);
        }
    }

    @Nested
    @DisplayName("isRootCategory() method tests")
    class IsRootCategoryTests {

        @Test
        @DisplayName("Should return non-null specification for root category check")
        void shouldReturnNonNullSpecificationForRootCategoryCheck() {
            // When
            Specification<Category> spec = CategorySpecification.isRootCategory();

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should handle multiple calls to isRootCategory")
        void shouldHandleMultipleCallsToIsRootCategory() {
            // When
            Specification<Category> spec1 = CategorySpecification.isRootCategory();
            Specification<Category> spec2 = CategorySpecification.isRootCategory();

            // Then
            assertNotNull(spec1);
            assertNotNull(spec2);
        }
    }

    @Nested
    @DisplayName("hasSubCategories() method tests")
    class HasSubCategoriesTests {

        @Test
        @DisplayName("Should return non-null specification for sub-categories check")
        void shouldReturnNonNullSpecificationForSubCategoriesCheck() {
            // When
            Specification<Category> spec = CategorySpecification.hasSubCategories();

            // Then
            assertNotNull(spec);
        }

        @Test
        @DisplayName("Should handle multiple calls to hasSubCategories")
        void shouldHandleMultipleCallsToHasSubCategories() {
            // When
            Specification<Category> spec1 = CategorySpecification.hasSubCategories();
            Specification<Category> spec2 = CategorySpecification.hasSubCategories();

            // Then
            assertNotNull(spec1);
            assertNotNull(spec2);
        }
    }

    @Nested
    @DisplayName("Specification composition tests")
    class SpecificationCompositionTests {

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
}
