package com.loiane.product.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ProductSpecification Unit Tests")
class ProductSpecificationTest {

    @ParameterizedTest
    @DisplayName("Should return non-null specification for various name inputs")
    @ValueSource(strings = {"Laptop", "MacBook Pro 14\"", "Samsung-2024!"})
    @NullAndEmptySource
    void shouldReturnNonNullSpecificationForVariousNames(String name) {
        // When
        Specification<Product> spec = ProductSpecification.hasName(name);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should handle very long names")
    void shouldHandleVeryLongNames() {
        // Given
        String longName = "A".repeat(1000);

        // When
        Specification<Product> spec = ProductSpecification.hasName(longName);

        // Then
        assertNotNull(spec);
    }

    @ParameterizedTest
    @DisplayName("Should return non-null specification for various status inputs")
    @ValueSource(strings = {"ACTIVE", "DRAFT", "DISCONTINUED", "active"})
    @NullAndEmptySource
    void shouldReturnNonNullSpecificationForVariousStatuses(String status) {
        // When
        Specification<Product> spec = ProductSpecification.hasStatus(status);

        // Then
        assertNotNull(spec);
    }

    @ParameterizedTest
    @DisplayName("Should return non-null specification for various brand inputs")
    @ValueSource(strings = {"Apple", "Samsung-2024!", "Niñé"})
    @NullAndEmptySource
    void shouldReturnNonNullSpecificationForVariousBrands(String brand) {
        // When
        Specification<Product> spec = ProductSpecification.hasBrand(brand);

        // Then
        assertNotNull(spec);
    }

    @ParameterizedTest
    @DisplayName("Should return non-null specification for various SKU inputs")
    @ValueSource(strings = {"MBP-2024", "ABC-123", "sku_001", "PRODUCT.2024", "12345"})
    @NullAndEmptySource
    void shouldReturnNonNullSpecificationForVariousSkus(String sku) {
        // When
        Specification<Product> spec = ProductSpecification.hasSku(sku);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for valid category ID")
    void shouldReturnNonNullSpecificationForValidCategoryId() {
        // Given
        UUID categoryId = UUID.randomUUID();

        // When
        Specification<Product> spec = ProductSpecification.hasCategory(categoryId);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for null category ID")
    void shouldReturnNonNullSpecificationForNullCategoryId() {
        // When
        Specification<Product> spec = ProductSpecification.hasCategory(null);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for valid category IDs set")
    void shouldReturnNonNullSpecificationForValidCategoryIds() {
        // Given
        Set<UUID> categoryIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        // When
        Specification<Product> spec = ProductSpecification.hasAnyCategory(categoryIds);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should return non-null specification for null/empty category IDs")
    void shouldReturnNonNullSpecificationForNullOrEmptyCategoryIds() {
        // Test null
        Specification<Product> nullSpec = ProductSpecification.hasAnyCategory(null);
        assertNotNull(nullSpec);

        // Test empty set
        Specification<Product> emptySpec = ProductSpecification.hasAnyCategory(Collections.emptySet());
        assertNotNull(emptySpec);
    }

    @Test
    @DisplayName("Should handle large set of category IDs")
    void shouldHandleLargeSetOfCategoryIds() {
        // Given
        Set<UUID> categoryIds = Set.of(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID()
        );

        // When
        Specification<Product> spec = ProductSpecification.hasAnyCategory(categoryIds);

        // Then
        assertNotNull(spec);
    }

    @Test
    @DisplayName("Should compose specifications with AND")
    void shouldComposeSpecificationsWithAnd() {
        // Given
        Specification<Product> nameSpec = ProductSpecification.hasName("Laptop");
        Specification<Product> statusSpec = ProductSpecification.hasStatus("ACTIVE");

        // When
        Specification<Product> combinedSpec = nameSpec.and(statusSpec);

        // Then
        assertNotNull(combinedSpec);
    }

    @Test
    @DisplayName("Should compose specifications with OR")
    void shouldComposeSpecificationsWithOr() {
        // Given
        Specification<Product> nameSpec = ProductSpecification.hasName("Laptop");
        Specification<Product> brandSpec = ProductSpecification.hasBrand("Apple");

        // When
        Specification<Product> combinedSpec = nameSpec.or(brandSpec);

        // Then
        assertNotNull(combinedSpec);
    }

    @Test
    @DisplayName("Should negate specifications")
    void shouldNegateSpecifications() {
        // Given
        Specification<Product> statusSpec = ProductSpecification.hasStatus("DRAFT");

        // When
        Specification<Product> negatedSpec = Specification.not(statusSpec);

        // Then
        assertNotNull(negatedSpec);
    }

    @Test
    @DisplayName("Should compose complex specifications")
    void shouldComposeComplexSpecifications() {
        // Given
        UUID categoryId = UUID.randomUUID();
        Set<UUID> categoryIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        // When
        Specification<Product> complexSpec = ProductSpecification.hasName("Laptop")
            .and(ProductSpecification.hasStatus("ACTIVE"))
            .and(ProductSpecification.hasBrand("Apple"))
            .and(ProductSpecification.hasCategory(categoryId))
            .or(ProductSpecification.hasAnyCategory(categoryIds));

        // Then
        assertNotNull(complexSpec);
    }
}
