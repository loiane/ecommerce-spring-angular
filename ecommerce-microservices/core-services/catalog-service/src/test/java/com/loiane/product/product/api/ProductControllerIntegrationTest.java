package com.loiane.product.product.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loiane.product.category.Category;
import com.loiane.product.category.CategoryRepository;
import com.loiane.product.common.BaseIntegrationTest;
import com.loiane.product.product.Product;
import com.loiane.product.product.ProductRepository;
import com.loiane.product.product.api.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@BaseIntegrationTest
@DisplayName("ProductController Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Clean up data before each test
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test category
        testCategory = new Category("Electronics", "electronics");
        testCategory = categoryRepository.save(testCategory);

        // Create test product
        testProduct = new Product("IP15-128GB", "iPhone 15", "iphone-15");
        testProduct.setBrand("Apple");
        testProduct.setDescription("Latest iPhone model");
        testProduct.setStatus("ACTIVE");
        testProduct.getCategories().add(testCategory);
        testProduct = productRepository.save(testProduct);
    }

    @Nested
    @DisplayName("GET /api/products")
    class ListProductsTests {

        @Test
        @DisplayName("Should return paginated products from database")
        void shouldReturnPaginatedProductsFromDatabase() throws Exception {
            mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("iPhone 15")))
                .andExpect(jsonPath("$.content[0].brand", is("Apple")))
                .andExpect(jsonPath("$.content[0].sku", is("IP15-128GB")))
                .andExpect(jsonPath("$.content[0].categories", hasSize(1)))
                .andExpect(jsonPath("$.content[0].categories[0].name", is("Electronics")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.size", is(20)));
        }

        @Test
        @DisplayName("Should return empty page when no products exist")
        void shouldReturnEmptyPageWhenNoProductsExist() throws Exception {
            productRepository.deleteAll();

            mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
        }

        @Test
        @DisplayName("Should support pagination parameters")
        void shouldSupportPaginationParameters() throws Exception {
            // Create additional products for pagination testing
            for (int i = 1; i <= 25; i++) {
                var product = new Product("SKU-" + i, "Product " + i, "product-" + i);
                productRepository.save(product);
            }

            mockMvc.perform(get("/api/products")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(26))) // 25 + 1 from setUp
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/products/search")
    class SearchProductsTests {

        @Test
        @DisplayName("Should search products by name from database")
        void shouldSearchProductsByNameFromDatabase() throws Exception {
            mockMvc.perform(get("/api/products/search")
                    .param("name", "iPhone"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("iPhone 15")));
        }

        @Test
        @DisplayName("Should search products by brand from database")
        void shouldSearchProductsByBrandFromDatabase() throws Exception {
            mockMvc.perform(get("/api/products/search")
                    .param("brand", "Apple"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].brand", is("Apple")));
        }

        @Test
        @DisplayName("Should search products by category from database")
        void shouldSearchProductsByCategoryFromDatabase() throws Exception {
            mockMvc.perform(get("/api/products/search")
                    .param("categoryIds", testCategory.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].categories[0].id", is(testCategory.getId().toString())));
        }

        @Test
        @DisplayName("Should return empty result for non-existent search criteria")
        void shouldReturnEmptyResultForNonExistentSearchCriteria() throws Exception {
            mockMvc.perform(get("/api/products/search")
                    .param("name", "NonExistentProduct"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProductTests {

        @Test
        @DisplayName("Should return product when found in database")
        void shouldReturnProductWhenFoundInDatabase() throws Exception {
            mockMvc.perform(get("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testProduct.getId().toString())))
                .andExpect(jsonPath("$.name", is("iPhone 15")))
                .andExpect(jsonPath("$.brand", is("Apple")))
                .andExpect(jsonPath("$.sku", is("IP15-128GB")))
                .andExpect(jsonPath("$.categories", hasSize(1)));
        }

        @Test
        @DisplayName("Should return 404 when product not found in database")
        void shouldReturn404WhenProductNotFoundInDatabase() throws Exception {
            var nonExistentId = UUID.randomUUID();

            mockMvc.perform(get("/api/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/products")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully in database")
        void shouldCreateProductSuccessfullyInDatabase() throws Exception {
            var request = new ProductRequest(
                "IP16-256GB",
                "iPhone 16",
                "iphone-16",
                "Apple",
                "Newest iPhone model",
                "ACTIVE",
                Set.of(testCategory.getId())
            );

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/products/")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("iPhone 16")))
                .andExpect(jsonPath("$.brand", is("Apple")))
                .andExpect(jsonPath("$.sku", is("IP16-256GB")))
                .andExpect(jsonPath("$.categories", hasSize(1)));

            // Verify product was actually saved to database
            var savedProducts = productRepository.findAll();
            assertThat(savedProducts).hasSize(2); // 1 from setUp + 1 created
        }

        @Test
        @DisplayName("Should return 400 for invalid product data")
        void shouldReturn400ForInvalidProductData() throws Exception {
            var invalidRequest = new ProductRequest(
                "", // Invalid empty SKU
                "iPhone 16",
                "iphone-16",
                "Apple",
                "Newest iPhone model",
                "ACTIVE",
                Set.of()
            );

            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

            // Verify no additional product was saved
            var savedProducts = productRepository.findAll();
            assertThat(savedProducts).hasSize(1); // Only the one from setUp
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully in database")
        void shouldUpdateProductSuccessfullyInDatabase() throws Exception {
            var request = new ProductRequest(
                "IP15-128GB-UPDATED",
                "iPhone 15 Updated",
                "iphone-15-updated",
                "Apple Inc.",
                "Updated description",
                "ACTIVE",
                Set.of(testCategory.getId())
            );

            mockMvc.perform(put("/api/products/{id}", testProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("iPhone 15 Updated")))
                .andExpect(jsonPath("$.brand", is("Apple Inc.")))
                .andExpect(jsonPath("$.sku", is("IP15-128GB-UPDATED")));

            // Verify product was actually updated in database
            var updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
            assertThat(updatedProduct.getName()).isEqualTo("iPhone 15 Updated");
            assertThat(updatedProduct.getBrand()).isEqualTo("Apple Inc.");
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent product")
        void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
            var nonExistentId = UUID.randomUUID();
            var request = new ProductRequest(
                "NEW-SKU",
                "New Product",
                "new-product",
                "Brand",
                "Description",
                "ACTIVE",
                Set.of()
            );

            mockMvc.perform(put("/api/products/{id}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product successfully from database")
        void shouldDeleteProductSuccessfullyFromDatabase() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isNoContent());

            // Verify product was actually deleted from database
            var deletedProduct = productRepository.findById(testProduct.getId());
            assertThat(deletedProduct).isEmpty();
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent product")
        void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
            var nonExistentId = UUID.randomUUID();

            mockMvc.perform(delete("/api/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }
}