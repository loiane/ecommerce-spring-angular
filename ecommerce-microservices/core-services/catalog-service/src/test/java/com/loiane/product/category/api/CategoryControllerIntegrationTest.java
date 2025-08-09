package com.loiane.product.category.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loiane.product.category.Category;
import com.loiane.product.category.CategoryRepository;
import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.common.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@BaseIntegrationTest
@DisplayName("CategoryController Integration Tests")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testRootCategory;
    private Category testSubCategory;

    @BeforeEach
    void setUp() {
        // Clean up data before each test
        categoryRepository.deleteAll();

        // Create test root category
        testRootCategory = new Category("Electronics", "electronics");
        testRootCategory = categoryRepository.save(testRootCategory);

        // Create test sub category
        testSubCategory = new Category("Smartphones", "smartphones");
        testSubCategory.setParent(testRootCategory);
        testSubCategory = categoryRepository.save(testSubCategory);
    }

    @Nested
    @DisplayName("GET /api/categories")
    class ListCategoriesTests {

        @Test
        @DisplayName("Should return paginated categories from database")
        void shouldReturnPaginatedCategoriesFromDatabase() throws Exception {
            mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", isOneOf("Electronics", "Smartphones")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.size", is(20)));
        }

        @Test
        @DisplayName("Should return empty page when no categories exist")
        void shouldReturnEmptyPageWhenNoCategoriesExist() throws Exception {
            categoryRepository.deleteAll();

            mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
        }

        @Test
        @DisplayName("Should support pagination parameters")
        void shouldSupportPaginationParameters() throws Exception {
            // Create additional categories for pagination testing
            for (int i = 1; i <= 25; i++) {
                var category = new Category("Category " + i, "category-" + i);
                categoryRepository.save(category);
            }

            mockMvc.perform(get("/api/categories")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(27))) // 25 + 2 from setUp
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/categories/search")
    class SearchCategoriesTests {

        @Test
        @DisplayName("Should search categories by name from database")
        void shouldSearchCategoriesByNameFromDatabase() throws Exception {
            mockMvc.perform(get("/api/categories/search")
                    .param("name", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Electronics")));
        }

        @Test
        @DisplayName("Should search categories by slug from database")
        void shouldSearchCategoriesBySlugFromDatabase() throws Exception {
            mockMvc.perform(get("/api/categories/search")
                    .param("slug", "smartphones"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug", is("smartphones")));
        }

        @Test
        @DisplayName("Should search categories by parent ID from database")
        void shouldSearchCategoriesByParentIdFromDatabase() throws Exception {
            mockMvc.perform(get("/api/categories/search")
                    .param("parentId", testRootCategory.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Smartphones")));
        }

        @Test
        @DisplayName("Should search for root categories from database")
        void shouldSearchForRootCategoriesFromDatabase() throws Exception {
            mockMvc.perform(get("/api/categories/search")
                    .param("isRoot", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Electronics")));
        }

        @Test
        @DisplayName("Should return empty result for non-existent search criteria")
        void shouldReturnEmptyResultForNonExistentSearchCriteria() throws Exception {
            mockMvc.perform(get("/api/categories/search")
                    .param("name", "NonExistentCategory"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/categories/{id}")
    class GetCategoryTests {

        @Test
        @DisplayName("Should return category when found in database")
        void shouldReturnCategoryWhenFoundInDatabase() throws Exception {
            mockMvc.perform(get("/api/categories/{id}", testRootCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testRootCategory.getId().toString())))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andExpect(jsonPath("$.slug", is("electronics")));
        }

        @Test
        @DisplayName("Should return 404 when category not found in database")
        void shouldReturn404WhenCategoryNotFoundInDatabase() throws Exception {
            var nonExistentId = UUID.randomUUID();

            mockMvc.perform(get("/api/categories/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/categories")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create root category successfully in database")
        void shouldCreateRootCategorySuccessfullyInDatabase() throws Exception {
            var request = new CategoryRequest(
                "Books",
                "books",
                null // No parent - root category
            );

            mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/categories/")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Books")))
                .andExpect(jsonPath("$.slug", is("books")));

            // Verify category was actually saved to database
            var savedCategories = categoryRepository.findAll();
            assertThat(savedCategories).hasSize(3); // 2 from setUp + 1 created
        }

        @Test
        @DisplayName("Should create sub category successfully in database")
        void shouldCreateSubCategorySuccessfullyInDatabase() throws Exception {
            var request = new CategoryRequest(
                "Tablets",
                "tablets",
                testRootCategory.getId()
            );

            mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/categories/")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Tablets")))
                .andExpect(jsonPath("$.slug", is("tablets")))
                .andExpect(jsonPath("$.parent.id", is(testRootCategory.getId().toString())));

            // Verify category was actually saved to database
            var savedCategories = categoryRepository.findAll();
            assertThat(savedCategories).hasSize(3); // 2 from setUp + 1 created
        }

        @Test
        @DisplayName("Should return 400 for invalid category data")
        void shouldReturn400ForInvalidCategoryData() throws Exception {
            var invalidRequest = new CategoryRequest(
                "", // Invalid empty name
                "books",
                null
            );

            mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

            // Verify no additional category was saved
            var savedCategories = categoryRepository.findAll();
            assertThat(savedCategories).hasSize(2); // Only the ones from setUp
        }
    }

    @Nested
    @DisplayName("PUT /api/categories/{id}")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully in database")
        void shouldUpdateCategorySuccessfullyInDatabase() throws Exception {
            var request = new CategoryRequest(
                "Electronics Updated",
                "electronics-updated",
                null
            );

            mockMvc.perform(put("/api/categories/{id}", testRootCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Electronics Updated")))
                .andExpect(jsonPath("$.slug", is("electronics-updated")));

            // Verify category was actually updated in database
            var updatedCategory = categoryRepository.findById(testRootCategory.getId()).orElseThrow();
            assertThat(updatedCategory.getName()).isEqualTo("Electronics Updated");
            assertThat(updatedCategory.getSlug()).isEqualTo("electronics-updated");
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent category")
        void shouldReturn404WhenUpdatingNonExistentCategory() throws Exception {
            var nonExistentId = UUID.randomUUID();
            var request = new CategoryRequest(
                "New Category",
                "new-category",
                null
            );

            mockMvc.perform(put("/api/categories/{id}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/categories/{id}")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully from database")
        void shouldDeleteCategorySuccessfullyFromDatabase() throws Exception {
            mockMvc.perform(delete("/api/categories/{id}", testSubCategory.getId()))
                .andExpect(status().isNoContent());

            // Verify category was actually deleted from database
            var deletedCategory = categoryRepository.findById(testSubCategory.getId());
            assertThat(deletedCategory).isEmpty();
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent category")
        void shouldReturn404WhenDeletingNonExistentCategory() throws Exception {
            var nonExistentId = UUID.randomUUID();

            mockMvc.perform(delete("/api/categories/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }
}