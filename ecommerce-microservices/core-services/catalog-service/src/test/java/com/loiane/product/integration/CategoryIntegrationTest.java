package com.loiane.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.dto.CategoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Category API Integration Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CategoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/categories")
    class ListCategoriesTests {

        @Test
        @DisplayName("Should return paginated categories from sample data")
        void shouldReturnPaginatedCategories() {
            // When
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();
            assertThat(response.getBody().getTotalElements()).isGreaterThan(0);

            // Verify sample data categories are present
            var categories = response.getBody().getContent();
            assertThat(categories).anyMatch(cat -> "Electronics".equals(cat.name()));
            assertThat(categories).anyMatch(cat -> "Fashion".equals(cat.name()));
        }

        @Test
        @DisplayName("Should respect pagination parameters")
        void shouldRespectPaginationParameters() {
            // When
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl() + "?page=0&size=5",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(5);
            assertThat(response.getBody().getNumber()).isEqualTo(0);
            assertThat(response.getBody().getSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should sort categories by name")
        void shouldSortCategoriesByName() {
            // When
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl() + "?sort=name,asc",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var categories = response.getBody().getContent();
            assertThat(categories).hasSize(20); // Default size

            // Verify sorting - names should be in alphabetical order
            for (int i = 1; i < categories.size(); i++) {
                assertThat(categories.get(i).name())
                        .isGreaterThanOrEqualTo(categories.get(i - 1).name());
            }
        }
    }

    @Nested
    @DisplayName("GET /api/categories/search")
    class SearchCategoriesTests {

        @Test
        @DisplayName("Should search categories by name")
        void shouldSearchCategoriesByName() {
            // When
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=Electronics",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(1);
            assertThat(response.getBody().getContent().get(0).name()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("Should search categories by slug")
        void shouldSearchCategoriesBySlug() {
            // When
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl() + "/search?slug=electronics",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(1);
            assertThat(response.getBody().getContent().get(0).slug()).isEqualTo("electronics");
        }

        @Test
        @DisplayName("Should search root categories only")
        void shouldSearchRootCategoriesOnly() {
            // When
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl() + "/search?isRoot=true",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSizeGreaterThanOrEqualTo(5); // At least 5 root categories from sample data

            // All returned categories should have no parent
            response.getBody().getContent().forEach(category ->
                assertThat(category.parent()).isNull()
            );
        }

        @Test
        @DisplayName("Should search subcategories by parent ID")
        void shouldSearchSubcategoriesByParentId() {
            // First, get the Electronics category ID
            ResponseEntity<RestPageImpl<CategoryResponse>> electronicsResponse = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=Electronics",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            UUID electronicsId = electronicsResponse.getBody().getContent().get(0).id();

            // When - search for subcategories of Electronics
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl() + "/search?parentId=" + electronicsId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSizeGreaterThanOrEqualTo(5); // At least 5 subcategories of Electronics

            // All returned categories should have Electronics as parent
            response.getBody().getContent().forEach(category -> {
                assertThat(category.parent()).isNotNull();
                assertThat(category.parent().id()).isEqualTo(electronicsId);
            });
        }

        @Test
        @DisplayName("Should return empty result for non-existent category name")
        void shouldReturnEmptyResultForNonExistentName() {
            // When
            ResponseEntity<RestPageImpl<CategoryResponse>> response = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=NonExistentCategory",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isEmpty();
            assertThat(response.getBody().getTotalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("GET /api/categories/{id}")
    class GetCategoryTests {

        @Test
        @DisplayName("Should return category by ID")
        void shouldReturnCategoryById() {
            // First, get a category ID from the list
            ResponseEntity<RestPageImpl<CategoryResponse>> listResponse = restTemplate.exchange(
                    getCategoriesUrl() + "?size=1",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            UUID categoryId = listResponse.getBody().getContent().get(0).id();

            // When
            ResponseEntity<CategoryResponse> response = restTemplate.getForEntity(
                    getCategoriesUrl() + "/" + categoryId,
                    CategoryResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(categoryId);
            assertThat(response.getBody().name()).isNotBlank();
            assertThat(response.getBody().slug()).isNotBlank();
        }

        @Test
        @DisplayName("Should return 404 for non-existent category ID")
        void shouldReturn404ForNonExistentId() {
            // When
            UUID nonExistentId = UUID.randomUUID();
            ResponseEntity<String> response = restTemplate.getForEntity(
                    getCategoriesUrl() + "/" + nonExistentId,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("POST /api/categories")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create new root category")
        void shouldCreateNewRootCategory() {
            // Given
            CategoryRequest request = new CategoryRequest("Test Category", "test-category", null);

            // When
            ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(
                    getCategoriesUrl(),
                    request,
                    CategoryResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Test Category");
            assertThat(response.getBody().slug()).isEqualTo("test-category");
            assertThat(response.getBody().parent()).isNull();
            assertThat(response.getBody().id()).isNotNull();

            // Verify Location header
            assertThat(response.getHeaders().getLocation()).isNotNull();
            assertThat(response.getHeaders().getLocation().toString())
                    .contains("/api/categories/" + response.getBody().id());
        }

        @Test
        @DisplayName("Should create new subcategory")
        void shouldCreateNewSubcategory() {
            // First, get a parent category ID
            ResponseEntity<RestPageImpl<CategoryResponse>> listResponse = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=Electronics",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            UUID parentId = listResponse.getBody().getContent().get(0).id();

            // Given
            CategoryRequest request = new CategoryRequest("Test Subcategory", "test-subcategory", parentId);

            // When
            ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(
                    getCategoriesUrl(),
                    request,
                    CategoryResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Test Subcategory");
            assertThat(response.getBody().slug()).isEqualTo("test-subcategory");
            assertThat(response.getBody().parent()).isNotNull();
            assertThat(response.getBody().parent().id()).isEqualTo(parentId);
        }

        @Test
        @DisplayName("Should return 400 for invalid category request")
        void shouldReturn400ForInvalidRequest() {
            // Given - invalid request with null name
            CategoryRequest request = new CategoryRequest(null, "test-slug", null);

            // When
            ResponseEntity<String> response = restTemplate.postForEntity(
                    getCategoriesUrl(),
                    request,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should return 409 for duplicate slug")
        void shouldReturn409ForDuplicateSlug() {
            // Given - request with existing slug
            CategoryRequest request = new CategoryRequest("Duplicate Electronics", "electronics", null);

            // When
            ResponseEntity<String> response = restTemplate.postForEntity(
                    getCategoriesUrl(),
                    request,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("PUT /api/categories/{id}")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update existing category")
        void shouldUpdateExistingCategory() {
            // First, create a category to update
            CategoryRequest createRequest = new CategoryRequest("Original Name", "original-slug", null);
            ResponseEntity<CategoryResponse> createResponse = restTemplate.postForEntity(
                    getCategoriesUrl(),
                    createRequest,
                    CategoryResponse.class
            );

            UUID categoryId = createResponse.getBody().id();

            // Given
            CategoryRequest updateRequest = new CategoryRequest("Updated Name", "updated-slug", null);

            // When
            ResponseEntity<CategoryResponse> response = restTemplate.exchange(
                    getCategoriesUrl() + "/" + categoryId,
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    CategoryResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(categoryId);
            assertThat(response.getBody().name()).isEqualTo("Updated Name");
            assertThat(response.getBody().slug()).isEqualTo("updated-slug");
        }

        @Test
        @DisplayName("Should return 404 for non-existent category ID in update")
        void shouldReturn404ForNonExistentIdInUpdate() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            CategoryRequest request = new CategoryRequest("Test Name", "test-slug", null);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    getCategoriesUrl() + "/" + nonExistentId,
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("DELETE /api/categories/{id}")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete existing category")
        void shouldDeleteExistingCategory() {
            // First, create a category to delete
            CategoryRequest createRequest = new CategoryRequest("To Delete", "to-delete", null);
            ResponseEntity<CategoryResponse> createResponse = restTemplate.postForEntity(
                    getCategoriesUrl(),
                    createRequest,
                    CategoryResponse.class
            );

            UUID categoryId = createResponse.getBody().id();

            // When
            ResponseEntity<Void> response = restTemplate.exchange(
                    getCategoriesUrl() + "/" + categoryId,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            // Verify category is deleted
            ResponseEntity<String> getResponse = restTemplate.getForEntity(
                    getCategoriesUrl() + "/" + categoryId,
                    String.class
            );
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Should return 404 for non-existent category ID in delete")
        void shouldReturn404ForNonExistentIdInDelete() {
            // When
            UUID nonExistentId = UUID.randomUUID();
            ResponseEntity<String> response = restTemplate.exchange(
                    getCategoriesUrl() + "/" + nonExistentId,
                    HttpMethod.DELETE,
                    null,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
