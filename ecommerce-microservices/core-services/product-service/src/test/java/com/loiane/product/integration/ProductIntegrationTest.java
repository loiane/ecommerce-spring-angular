package com.loiane.product.integration;

import com.loiane.product.category.api.dto.CategoryResponse;
import com.loiane.product.product.api.dto.ProductRequest;
import com.loiane.product.product.api.dto.ProductResponse;
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

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product API Integration Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("GET /api/products")
    class ListProductsTests {

        @Test
        @DisplayName("Should return paginated products from sample data")
        void shouldReturnPaginatedProducts() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();
            assertThat(response.getBody().getTotalElements()).isGreaterThan(0);

            // Verify sample data products are present
            var products = response.getBody().getContent();
            assertThat(products)
                .anyMatch(product -> product.name().contains("MacBook") || product.name().contains("Galaxy"))
                .anyMatch(product -> product.brand().equals("Apple") || product.brand().equals("Samsung"));
        }

        @Test
        @DisplayName("Should respect pagination parameters")
        void shouldRespectPaginationParameters() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "?page=0&size=3",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(3);
            assertThat(response.getBody().getNumber()).isZero();
            assertThat(response.getBody().getSize()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should sort products by name")
        void shouldSortProductsByName() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "?sort=name,asc&size=10",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var products = response.getBody().getContent();
            assertThat(products).hasSizeGreaterThan(1);

            // Verify sorting - names should be in alphabetical order
            for (int i = 1; i < products.size(); i++) {
                assertThat(products.get(i).name())
                        .isGreaterThanOrEqualTo(products.get(i - 1).name());
            }
        }
    }

    @Nested
    @DisplayName("GET /api/products/search")
    class SearchProductsTests {

        @Test
        @DisplayName("Should search products by name")
        void shouldSearchProductsByName() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "/search?name=MacBook",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();

            // All returned products should contain "MacBook" in the name
            response.getBody().getContent().forEach(product ->
                assertThat(product.name().toLowerCase()).contains("macbook")
            );
        }

        @Test
        @DisplayName("Should search products by brand")
        void shouldSearchProductsByBrand() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "/search?brand=Apple",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();

            // All returned products should be from Apple
            response.getBody().getContent().forEach(product ->
                assertThat(product.brand()).isEqualTo("Apple")
            );
        }

        @Test
        @DisplayName("Should search products by status")
        void shouldSearchProductsByStatus() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "/search?status=ACTIVE",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();

            // All returned products should be ACTIVE
            response.getBody().getContent().forEach(product ->
                assertThat(product.status()).isEqualTo("ACTIVE")
            );
        }

        @Test
        @DisplayName("Should search products by SKU")
        void shouldSearchProductsBySku() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "/search?sku=MBP16",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();

            // All returned products should have SKU containing "MBP16"
            response.getBody().getContent().forEach(product ->
                assertThat(product.sku().toLowerCase()).contains("mbp16")
            );
        }

        @Test
        @DisplayName("Should search products by category")
        void shouldSearchProductsByCategory() {
            // First, get the Smartphones category ID
            ResponseEntity<RestPageImpl<CategoryResponse>> categoryResponse = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=Smartphones",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            UUID smartphonesCategoryId = categoryResponse.getBody().getContent().get(0).id();

            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "/search?categoryIds=" + smartphonesCategoryId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();

            // All returned products should be in the Smartphones category
            response.getBody().getContent().forEach(product ->
                assertThat(product.categories()).anyMatch(category ->
                    category.id().equals(smartphonesCategoryId)
                )
            );
        }

        @Test
        @DisplayName("Should combine multiple search criteria")
        void shouldCombineMultipleSearchCriteria() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "/search?brand=Apple&status=ACTIVE",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isNotEmpty();

            // All returned products should match both criteria
            response.getBody().getContent().forEach(product -> {
                assertThat(product.brand()).isEqualTo("Apple");
                assertThat(product.status()).isEqualTo("ACTIVE");
            });
        }

        @Test
        @DisplayName("Should return empty result for non-existent product name")
        void shouldReturnEmptyResultForNonExistentName() {
            // When
            ResponseEntity<RestPageImpl<ProductResponse>> response = restTemplate.exchange(
                    getProductsUrl() + "/search?name=NonExistentProduct",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isEmpty();
            assertThat(response.getBody().getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProductTests {

        @Test
        @DisplayName("Should return product by ID")
        void shouldReturnProductById() {
            // First, get a product ID from the list
            ResponseEntity<RestPageImpl<ProductResponse>> listResponse = restTemplate.exchange(
                    getProductsUrl() + "?size=1",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            UUID productId = listResponse.getBody().getContent().get(0).id();

            // When
            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
                    getProductsUrl() + "/" + productId,
                    ProductResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(productId);
            assertThat(response.getBody().name()).isNotBlank();
            assertThat(response.getBody().sku()).isNotBlank();
            assertThat(response.getBody().slug()).isNotBlank();
            assertThat(response.getBody().status()).isNotBlank();
        }

        @Test
        @DisplayName("Should return product with category information")
        void shouldReturnProductWithCategoryInformation() {
            // First, get a product that has categories
            ResponseEntity<RestPageImpl<ProductResponse>> listResponse = restTemplate.exchange(
                    getProductsUrl() + "?size=1",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<ProductResponse>>() {}
            );

            UUID productId = listResponse.getBody().getContent().get(0).id();

            // When
            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
                    getProductsUrl() + "/" + productId,
                    ProductResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().categories()).isNotEmpty();

            // Verify category information is complete
            response.getBody().categories().forEach(category -> {
                assertThat(category.id()).isNotNull();
                assertThat(category.name()).isNotBlank();
                assertThat(category.slug()).isNotBlank();
            });
        }

        @Test
        @DisplayName("Should return 404 for non-existent product ID")
        void shouldReturn404ForNonExistentId() {
            // When
            UUID nonExistentId = UUID.randomUUID();
            ResponseEntity<String> response = restTemplate.getForEntity(
                    getProductsUrl() + "/" + nonExistentId,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("POST /api/products")
    class CreateProductTests {

        @Test
        @DisplayName("Should create new product")
        void shouldCreateNewProduct() {
            // First, get a category ID for the product
            ResponseEntity<RestPageImpl<CategoryResponse>> categoryResponse = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=Smartphones",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            UUID categoryId = categoryResponse.getBody().getContent().get(0).id();

            // Given
            ProductRequest request = new ProductRequest(
                    "TEST-SKU-001",
                    "Test Product",
                    "test-product",
                    "Test Brand",
                    "A test product for integration testing",
                    "ACTIVE",
                    Set.of(categoryId)
            );

            // When
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                    getProductsUrl(),
                    request,
                    ProductResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().sku()).isEqualTo("TEST-SKU-001");
            assertThat(response.getBody().name()).isEqualTo("Test Product");
            assertThat(response.getBody().slug()).isEqualTo("test-product");
            assertThat(response.getBody().brand()).isEqualTo("Test Brand");
            assertThat(response.getBody().status()).isEqualTo("ACTIVE"); // Default status
            assertThat(response.getBody().id()).isNotNull();
            assertThat(response.getBody().categories()).hasSize(1);

            // Verify Location header
            assertThat(response.getHeaders().getLocation()).isNotNull();
            assertThat(response.getHeaders().getLocation().toString())
                    .contains("/api/products/" + response.getBody().id());
        }

        @Test
        @DisplayName("Should create product with multiple categories")
        void shouldCreateProductWithMultipleCategories() {
            // Get multiple category IDs
            ResponseEntity<RestPageImpl<CategoryResponse>> electronicsResponse = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=Electronics",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );
            ResponseEntity<RestPageImpl<CategoryResponse>> smartphonesResponse = restTemplate.exchange(
                    getCategoriesUrl() + "/search?name=Smartphones",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<RestPageImpl<CategoryResponse>>() {}
            );

            UUID electronicsId = electronicsResponse.getBody().getContent().get(0).id();
            UUID smartphonesId = smartphonesResponse.getBody().getContent().get(0).id();

            // Given
            ProductRequest request = new ProductRequest(
                    "TEST-SKU-002",
                    "Multi-Category Product",
                    "multi-category-product",
                    "Test Brand",
                    "A product in multiple categories",
                    "ACTIVE",
                    Set.of(electronicsId, smartphonesId)
            );

            // When
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                    getProductsUrl(),
                    request,
                    ProductResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().categories()).hasSize(2);

            var categoryIds = response.getBody().categories().stream()
                    .map(ProductResponse.CategorySummary::id)
                    .toList();
            assertThat(categoryIds).containsExactlyInAnyOrder(electronicsId, smartphonesId);
        }

        @Test
        @DisplayName("Should return 400 for invalid product request")
        void shouldReturn400ForInvalidRequest() {
            // Given - invalid request with null name
            ProductRequest request = new ProductRequest(
                    "TEST-SKU-003",
                    null, // Invalid: null name
                    "test-slug",
                    "Brand",
                    "Description",
                    "ACTIVE",
                    Set.of()
            );

            // When
            ResponseEntity<String> response = restTemplate.postForEntity(
                    getProductsUrl(),
                    request,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should return 400 for duplicate SKU")
        void shouldReturn400ForDuplicateSku() {
            // Given - request with existing SKU from sample data
            ProductRequest request = new ProductRequest(
                    "MBP16-M3-512", // Existing SKU from sample data
                    "Duplicate MacBook",
                    "duplicate-macbook",
                    "Apple",
                    "A duplicate MacBook",
                    "ACTIVE",
                    Set.of()
            );

            // When
            ResponseEntity<String> response = restTemplate.postForEntity(
                    getProductsUrl(),
                    request,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update existing product")
        void shouldUpdateExistingProduct() {
            // First, create a product to update
            ProductRequest createRequest = new ProductRequest(
                    "TEST-SKU-UPDATE",
                    "Original Product",
                    "original-product",
                    "Original Brand",
                    "Original description",
                    "ACTIVE",
                    Set.of()
            );
            ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                    getProductsUrl(),
                    createRequest,
                    ProductResponse.class
            );

            UUID productId = createResponse.getBody().id();

            // Given
            ProductRequest updateRequest = new ProductRequest(
                    "TEST-SKU-UPDATED",
                    "Updated Product",
                    "updated-product",
                    "Updated Brand",
                    "Updated description",
                    "ACTIVE",
                    Set.of()
            );

            // When
            ResponseEntity<ProductResponse> response = restTemplate.exchange(
                    getProductsUrl() + "/" + productId,
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    ProductResponse.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isEqualTo(productId);
            assertThat(response.getBody().sku()).isEqualTo("TEST-SKU-UPDATED");
            assertThat(response.getBody().name()).isEqualTo("Updated Product");
            assertThat(response.getBody().slug()).isEqualTo("updated-product");
            assertThat(response.getBody().brand()).isEqualTo("Updated Brand");
        }

        @Test
        @DisplayName("Should return 404 for non-existent product ID in update")
        void shouldReturn404ForNonExistentIdInUpdate() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            ProductRequest request = new ProductRequest(
                    "TEST-SKU-404",
                    "Test Product",
                    "test-product",
                    "Brand",
                    "Description",
                    "ACTIVE",
                    Set.of()
            );

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    getProductsUrl() + "/" + nonExistentId,
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete existing product")
        void shouldDeleteExistingProduct() {
            // First, create a product to delete
            ProductRequest createRequest = new ProductRequest(
                    "TEST-SKU-DELETE",
                    "To Delete Product",
                    "to-delete-product",
                    "Delete Brand",
                    "Product to be deleted",
                    "ACTIVE",
                    Set.of()
            );
            ResponseEntity<ProductResponse> createResponse = restTemplate.postForEntity(
                    getProductsUrl(),
                    createRequest,
                    ProductResponse.class
            );

            UUID productId = createResponse.getBody().id();

            // When
            ResponseEntity<Void> response = restTemplate.exchange(
                    getProductsUrl() + "/" + productId,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            // Verify product is deleted
            ResponseEntity<String> getResponse = restTemplate.getForEntity(
                    getProductsUrl() + "/" + productId,
                    String.class
            );
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Should return 404 for non-existent product ID in delete")
        void shouldReturn404ForNonExistentIdInDelete() {
            // When
            UUID nonExistentId = UUID.randomUUID();
            ResponseEntity<String> response = restTemplate.exchange(
                    getProductsUrl() + "/" + nonExistentId,
                    HttpMethod.DELETE,
                    null,
                    String.class
            );

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
