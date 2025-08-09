package com.loiane.product.category.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loiane.product.category.CategoryService;
import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@DisplayName("CategoryController Unit Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryResponse createTestCategoryResponse() {
        return new CategoryResponse(
            UUID.randomUUID(),
            "Electronics",
            "electronics",
            null, // parentId
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }

    private CategoryRequest createTestCategoryRequest() {
        return new CategoryRequest(
            "Electronics",
            "electronics",
            null // parentId
        );
    }

    @Nested
    @DisplayName("GET /api/categories")
    class ListCategoriesTests {

        @Test
        @DisplayName("Should return paginated categories with default pagination")
        void shouldReturnPaginatedCategoriesWithDefaultPagination() throws Exception {
            // Given
            var category = createTestCategoryResponse();
            var page = new PageImpl<>(List.of(category), PageRequest.of(0, 20, Sort.by("name")), 1);

            when(categoryService.listAll(any(Pageable.class))).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Electronics")))
                .andExpect(jsonPath("$.content[0].slug", is("electronics")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.size", is(20)));

            verify(categoryService).listAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no categories exist")
        void shouldReturnEmptyPageWhenNoCategoriesExist() throws Exception {
            // Given
            var emptyPage = new PageImpl<CategoryResponse>(List.of(), PageRequest.of(0, 20), 0);

            when(categoryService.listAll(any(Pageable.class))).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

            verify(categoryService).listAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should accept custom pagination parameters")
        void shouldAcceptCustomPaginationParameters() throws Exception {
            // Given
            var emptyPage = new PageImpl<CategoryResponse>(List.of(), PageRequest.of(1, 5), 0);

            when(categoryService.listAll(any(Pageable.class))).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/categories")
                    .param("page", "1")
                    .param("size", "5")
                    .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)));

            verify(categoryService).listAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/categories/{id}")
    class GetCategoryTests {

        @Test
        @DisplayName("Should return category when found")
        void shouldReturnCategoryWhenFound() throws Exception {
            // Given
            var categoryId = UUID.randomUUID();
            var category = createTestCategoryResponse();

            when(categoryService.getById(categoryId)).thenReturn(category);

            // When & Then
            mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andExpect(jsonPath("$.slug", is("electronics")));

            verify(categoryService).getById(categoryId);
        }

        @Test
        @DisplayName("Should return 404 when category not found")
        void shouldReturn404WhenCategoryNotFound() throws Exception {
            // Given
            var categoryId = UUID.randomUUID();

            when(categoryService.getById(categoryId))
                .thenThrow(new EntityNotFoundException("Category not found: " + categoryId));

            // When & Then
            mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isNotFound());

            verify(categoryService).getById(categoryId);
        }
    }

    @Nested
    @DisplayName("POST /api/categories")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategorySuccessfully() throws Exception {
            // Given
            var request = createTestCategoryRequest();
            var response = createTestCategoryResponse();

            when(categoryService.create(any(CategoryRequest.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/categories/")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andExpect(jsonPath("$.slug", is("electronics")));

            verify(categoryService).create(any(CategoryRequest.class));
        }

        @Test
        @DisplayName("Should return 400 for invalid request")
        void shouldReturn400ForInvalidRequest() throws Exception {
            // Given
            var invalidRequest = new CategoryRequest(
                "", // Invalid empty name
                "electronics",
                null
            );

            // When & Then
            mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

            verify(categoryService, never()).create(any(CategoryRequest.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/categories/{id}")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully() throws Exception {
            // Given
            var categoryId = UUID.randomUUID();
            var request = createTestCategoryRequest();
            var response = createTestCategoryResponse();

            when(categoryService.update(eq(categoryId), any(CategoryRequest.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(put("/api/categories/{id}", categoryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andExpect(jsonPath("$.slug", is("electronics")));

            verify(categoryService).update(eq(categoryId), any(CategoryRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when category not found for update")
        void shouldReturn404WhenCategoryNotFoundForUpdate() throws Exception {
            // Given
            var categoryId = UUID.randomUUID();
            var request = createTestCategoryRequest();

            when(categoryService.update(eq(categoryId), any(CategoryRequest.class)))
                .thenThrow(new EntityNotFoundException("Category not found: " + categoryId));

            // When & Then
            mockMvc.perform(put("/api/categories/{id}", categoryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

            verify(categoryService).update(eq(categoryId), any(CategoryRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/categories/{id}")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategorySuccessfully() throws Exception {
            // Given
            var categoryId = UUID.randomUUID();

            doNothing().when(categoryService).delete(categoryId);

            // When & Then
            mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent());

            verify(categoryService).delete(categoryId);
        }

        @Test
        @DisplayName("Should return 404 when category not found for deletion")
        void shouldReturn404WhenCategoryNotFoundForDeletion() throws Exception {
            // Given
            var categoryId = UUID.randomUUID();

            doThrow(new EntityNotFoundException("Category not found: " + categoryId))
                .when(categoryService).delete(categoryId);

            // When & Then
            mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNotFound());

            verify(categoryService).delete(categoryId);
        }
    }
}
