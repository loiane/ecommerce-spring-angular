package com.loiane.product.product.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loiane.product.product.ProductService;
import com.loiane.product.product.api.dto.ProductRequest;
import com.loiane.product.product.api.dto.ProductResponse;
import com.loiane.product.common.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Unit Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponse createTestProductResponse() {
        return new ProductResponse(
            UUID.randomUUID(),
            "IP15-128GB", // sku
            "iPhone 15", // name
            "iphone-15", // slug
            "Apple", // brand
            "Latest iPhone model", // description
            "ACTIVE", // status
            List.of(), // categories
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }

    private ProductRequest createTestProductRequest() {
        return new ProductRequest(
            "IP15-128GB", // sku
            "iPhone 15", // name
            "iphone-15", // slug
            "Apple", // brand
            "Latest iPhone model", // description
            "ACTIVE", // status
            Set.of() // categoryIds
        );
    }

    @Nested
    @DisplayName("GET /api/products")
    class ListProductsTests {

        @Test
        @DisplayName("Should return paginated products with default pagination")
        void shouldReturnPaginatedProductsWithDefaultPagination() throws Exception {
            // Given
            var product = createTestProductResponse();
            var page = new PageImpl<>(List.of(product), PageRequest.of(0, 20, Sort.by("name")), 1);

            when(productService.listAll(any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("iPhone 15")))
                .andExpect(jsonPath("$.content[0].brand", is("Apple")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.size", is(20)));

            verify(productService).listAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no products exist")
        void shouldReturnEmptyPageWhenNoProductsExist() throws Exception {
            // Given
            var emptyPage = new PageImpl<ProductResponse>(List.of(), PageRequest.of(0, 20), 0);

            when(productService.listAll(any(Pageable.class))).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

            verify(productService).listAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should accept custom pagination parameters")
        void shouldAcceptCustomPaginationParameters() throws Exception {
            // Given
            var emptyPage = new PageImpl<ProductResponse>(List.of(), PageRequest.of(1, 5), 0);

            when(productService.listAll(any(Pageable.class))).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/products")
                    .param("page", "1")
                    .param("size", "5")
                    .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)));

            verify(productService).listAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/products/search")
    class SearchProductsTests {

        @Test
        @DisplayName("Should search products by name")
        void shouldSearchProductsByName() throws Exception {
            // Given
            var product = createTestProductResponse();
            var page = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);

            when(productService.search(eq("iPhone"), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/products/search")
                    .param("name", "iPhone"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("iPhone 15")));

            verify(productService).search(eq("iPhone"), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search products by multiple criteria")
        void shouldSearchProductsByMultipleCriteria() throws Exception {
            // Given
            var product = createTestProductResponse();
            var page = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);
            var categoryIds = Set.of(UUID.randomUUID());

            when(productService.search(eq("iPhone"), eq("ACTIVE"), eq("Apple"), isNull(), eq(categoryIds), any(Pageable.class)))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/products/search")
                    .param("name", "iPhone")
                    .param("status", "ACTIVE")
                    .param("brand", "Apple")
                    .param("categoryIds", categoryIds.iterator().next().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));

            verify(productService).search(eq("iPhone"), eq("ACTIVE"), eq("Apple"), isNull(), eq(categoryIds), any(Pageable.class));
        }

        @Test
        @DisplayName("Should search products with no criteria (return all)")
        void shouldSearchProductsWithNoCriteria() throws Exception {
            // Given
            var product = createTestProductResponse();
            var page = new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1);

            when(productService.search(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/products/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));

            verify(productService).search(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProductTests {

        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProductWhenFound() throws Exception {
            // Given
            var productId = UUID.randomUUID();
            var product = createTestProductResponse();

            when(productService.getById(productId)).thenReturn(product);

            // When & Then
            mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("iPhone 15")))
                .andExpect(jsonPath("$.brand", is("Apple")))
                .andExpect(jsonPath("$.sku", is("IP15-128GB")));

            verify(productService).getById(productId);
        }

        @Test
        @DisplayName("Should return 404 when product not found")
        void shouldReturn404WhenProductNotFound() throws Exception {
            // Given
            var productId = UUID.randomUUID();

            when(productService.getById(productId))
                .thenThrow(new ProductNotFoundException(productId));

            // When & Then
            mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());

            verify(productService).getById(productId);
        }
    }

    @Nested
    @DisplayName("POST /api/products")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProductSuccessfully() throws Exception {
            // Given
            var request = createTestProductRequest();
            var response = createTestProductResponse();

            when(productService.create(any(ProductRequest.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/products/")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("iPhone 15")))
                .andExpect(jsonPath("$.brand", is("Apple")));

            verify(productService).create(any(ProductRequest.class));
        }

        @Test
        @DisplayName("Should return 400 for invalid request")
        void shouldReturn400ForInvalidRequest() throws Exception {
            // Given
            var invalidRequest = new ProductRequest(
                "", // Invalid empty SKU
                "iPhone 15", // name
                "iphone-15", // slug
                "Apple", // brand
                "Description", // description
                "ACTIVE", // status
                Set.of() // categoryIds
            );

            // When & Then
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

            verify(productService, never()).create(any(ProductRequest.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() throws Exception {
            // Given
            var productId = UUID.randomUUID();
            var request = createTestProductRequest();
            var response = createTestProductResponse();

            when(productService.update(eq(productId), any(ProductRequest.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(put("/api/products/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("iPhone 15")))
                .andExpect(jsonPath("$.brand", is("Apple")));

            verify(productService).update(eq(productId), any(ProductRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when product not found for update")
        void shouldReturn404WhenProductNotFoundForUpdate() throws Exception {
            // Given
            var productId = UUID.randomUUID();
            var request = createTestProductRequest();

            when(productService.update(eq(productId), any(ProductRequest.class)))
                .thenThrow(new ProductNotFoundException(productId));

            // When & Then
            mockMvc.perform(put("/api/products/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

            verify(productService).update(eq(productId), any(ProductRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProductSuccessfully() throws Exception {
            // Given
            var productId = UUID.randomUUID();

            doNothing().when(productService).delete(productId);

            // When & Then
            mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

            verify(productService).delete(productId);
        }

        @Test
        @DisplayName("Should return 404 when product not found for deletion")
        void shouldReturn404WhenProductNotFoundForDeletion() throws Exception {
            // Given
            var productId = UUID.randomUUID();

            doThrow(new ProductNotFoundException(productId))
                .when(productService).delete(productId);

            // When & Then
            mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNotFound());

            verify(productService).delete(productId);
        }
    }
}
