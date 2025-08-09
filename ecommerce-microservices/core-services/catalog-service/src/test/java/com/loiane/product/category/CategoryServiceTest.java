package com.loiane.product.category;

import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
@SuppressWarnings("unchecked")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category createTestCategory() {
        return new Category("Electronics", "electronics");
    }

    private Category createTestCategoryWithId(UUID id) {
        var category = new Category("Electronics", "electronics");
        setId(category, id);
        return category;
    }

    private void setId(Category category, UUID id) {
        try {
            Field field = Category.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(category, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CategoryRequest createTestCategoryRequest() {
        return new CategoryRequest("Electronics", "electronics", null);
    }

    @Nested
    @DisplayName("listAll() method tests")
    class ListAllTests {

        @Test
        @DisplayName("Should return paginated categories")
        void shouldReturnPaginatedCategories() {
            // Given
            var category = createTestCategory();
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(category), pageable, 1);

            when(categoryRepository.findAll(pageable)).thenReturn(page);

            // When
            Page<CategoryResponse> result = categoryService.listAll(pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Electronics", result.getContent().get(0).name());
            verify(categoryRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should return empty page when no categories exist")
        void shouldReturnEmptyPageWhenNoCategoriesExist() {
            // Given
            var pageable = PageRequest.of(0, 10);
            var emptyPage = new PageImpl<Category>(List.of(), pageable, 0);

            when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

            // When
            Page<CategoryResponse> result = categoryService.listAll(pageable);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }

        @Test
        @DisplayName("Should return all categories as list")
        void shouldReturnAllCategoriesAsList() {
            // Given
            var category = createTestCategory();
            when(categoryRepository.findAll()).thenReturn(List.of(category));

            // When
            List<CategoryResponse> result = categoryService.listAll();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Electronics", result.get(0).name());
            verify(categoryRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no categories exist")
        void shouldReturnEmptyListWhenNoCategoriesExist() {
            // Given
            when(categoryRepository.findAll()).thenReturn(List.of());

            // When
            List<CategoryResponse> result = categoryService.listAll();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("search() method tests")
    class SearchTests {

        @Test
        @DisplayName("Should search by name")
        void shouldSearchByName() {
            // Given
            var category = createTestCategory();
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(category), pageable, 1);

            when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            // When
            Page<CategoryResponse> result = categoryService.search("Electronics", null, null, null, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Electronics", result.getContent().get(0).name());
            verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should search by slug")
        void shouldSearchBySlug() {
            // Given
            var category = createTestCategory();
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(category), pageable, 1);

            when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            // When
            Page<CategoryResponse> result = categoryService.search(null, "electronics", null, null, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should search by parent ID")
        void shouldSearchByParentId() {
            // Given
            var parentId = UUID.randomUUID();
            var category = createTestCategory();
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(category), pageable, 1);

            when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            // When
            Page<CategoryResponse> result = categoryService.search(null, null, parentId, null, pageable);

            // Then
            assertNotNull(result);
            verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
        }

        @ParameterizedTest
        @MethodSource("searchCriteriaProvider")
        @DisplayName("Should search categories with different criteria")
        void shouldSearchCategoriesWithDifferentCriteria(String name, String slug, UUID parentId, Boolean rootCategory, String testDescription) {
            // Given
            var category = createTestCategory();
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(category), pageable, 1);

            when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            // When
            Page<CategoryResponse> result = categoryService.search(name, slug, parentId, rootCategory, pageable);

            // Then
            assertNotNull(result);
            verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
        }

        static Stream<Arguments> searchCriteriaProvider() {
            return Stream.of(
                Arguments.of(null, null, null, true, "Should search for root categories"),
                Arguments.of(null, null, null, false, "Should search for non-root categories"),
                Arguments.of("Electronics", "electronics", null, true, "Should search with multiple criteria")
            );
        }

        @Test
        @DisplayName("Should return all categories when no search criteria provided")
        void shouldReturnAllCategoriesWhenNoSearchCriteriaProvided() {
            // Given
            var category = createTestCategory();
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(category), pageable, 1);

            when(categoryRepository.findAll(pageable)).thenReturn(page);

            // When
            Page<CategoryResponse> result = categoryService.search(null, null, null, null, pageable);

            // Then
            assertNotNull(result);
            verify(categoryRepository).findAll(pageable);
            verify(categoryRepository, never()).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should ignore empty string criteria")
        void shouldIgnoreEmptyStringCriteria() {
            // Given
            var category = createTestCategory();
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(category), pageable, 1);

            when(categoryRepository.findAll(pageable)).thenReturn(page);

            // When
            Page<CategoryResponse> result = categoryService.search("", "   ", null, null, pageable);

            // Then
            assertNotNull(result);
            verify(categoryRepository).findAll(pageable);
            verify(categoryRepository, never()).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    @DisplayName("getById() method tests")
    class GetByIdTests {

        @Test
        @DisplayName("Should return category when found")
        void shouldReturnCategoryWhenFound() {
            // Given
            var categoryId = UUID.randomUUID();
            var category = createTestCategoryWithId(categoryId);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            // When
            CategoryResponse result = categoryService.getById(categoryId);

            // Then
            assertNotNull(result);
            assertEquals(categoryId, result.id());
            assertEquals("Electronics", result.name());
            verify(categoryRepository).findById(categoryId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when category not found")
        void shouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {
            // Given
            var categoryId = UUID.randomUUID();

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(categoryId)
            );

            assertEquals("Category not found: " + categoryId, exception.getMessage());
            verify(categoryRepository).findById(categoryId);
        }
    }

    @Nested
    @DisplayName("create() method tests")
    class CreateTests {

        @Test
        @DisplayName("Should create category without parent")
        void shouldCreateCategoryWithoutParent() {
            // Given
            var request = createTestCategoryRequest();
            var category = createTestCategory();

            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // When
            CategoryResponse result = categoryService.create(request);

            // Then
            assertNotNull(result);
            assertEquals("Electronics", result.name());
            verify(categoryRepository).save(any(Category.class));
            verify(categoryRepository, never()).findById(any(UUID.class));
        }

        @Test
        @DisplayName("Should create category with parent")
        void shouldCreateCategoryWithParent() {
            // Given
            var parentId = UUID.randomUUID();
            var parent = createTestCategoryWithId(parentId);

            var request = new CategoryRequest("Smartphones", "smartphones", parentId);

            var category = new Category("Smartphones", "smartphones");
            category.setParent(parent);

            when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // When
            CategoryResponse result = categoryService.create(request);

            // Then
            assertNotNull(result);
            assertEquals("Smartphones", result.name());
            verify(categoryRepository).findById(parentId);
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when parent not found")
        void shouldThrowEntityNotFoundExceptionWhenParentNotFound() {
            // Given
            var parentId = UUID.randomUUID();
            var request = new CategoryRequest("Smartphones", "smartphones", parentId);

            when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.create(request)
            );

            assertEquals("Parent category not found: " + parentId, exception.getMessage());
            verify(categoryRepository).findById(parentId);
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("update() method tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update category without parent")
        void shouldUpdateCategoryWithoutParent() {
            // Given
            var categoryId = UUID.randomUUID();
            var category = createTestCategoryWithId(categoryId);

            var request = new CategoryRequest("Updated Electronics", "updated-electronics", null);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            // When
            CategoryResponse result = categoryService.update(categoryId, request);

            // Then
            assertNotNull(result);
            assertEquals("Updated Electronics", result.name());
            verify(categoryRepository).findById(categoryId);
        }

        @Test
        @DisplayName("Should update category with new parent")
        void shouldUpdateCategoryWithNewParent() {
            // Given
            var categoryId = UUID.randomUUID();
            var parentId = UUID.randomUUID();

            var category = createTestCategoryWithId(categoryId);
            var parent = createTestCategoryWithId(parentId);

            var request = new CategoryRequest("Updated Electronics", "updated-electronics", parentId);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));

            // When
            CategoryResponse result = categoryService.update(categoryId, request);

            // Then
            assertNotNull(result);
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).findById(parentId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when category not found")
        void shouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {
            // Given
            var categoryId = UUID.randomUUID();
            var request = createTestCategoryRequest();

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(categoryId, request)
            );

            assertEquals("Category not found: " + categoryId, exception.getMessage());
            verify(categoryRepository).findById(categoryId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when parent not found")
        void shouldThrowEntityNotFoundExceptionWhenParentNotFound() {
            // Given
            var categoryId = UUID.randomUUID();
            var parentId = UUID.randomUUID();

            var category = createTestCategoryWithId(categoryId);
            var request = new CategoryRequest("Updated Electronics", "updated-electronics", parentId);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(categoryId, request)
            );

            assertEquals("Parent category not found: " + parentId, exception.getMessage());
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).findById(parentId);
        }
    }

    @Nested
    @DisplayName("delete() method tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete category when it exists")
        void shouldDeleteCategoryWhenItExists() {
            // Given
            var categoryId = UUID.randomUUID();

            when(categoryRepository.existsById(categoryId)).thenReturn(true);

            // When
            categoryService.delete(categoryId);

            // Then
            verify(categoryRepository).existsById(categoryId);
            verify(categoryRepository).deleteById(categoryId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when category not found")
        void shouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {
            // Given
            var categoryId = UUID.randomUUID();

            when(categoryRepository.existsById(categoryId)).thenReturn(false);

            // When & Then
            EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.delete(categoryId)
            );

            assertEquals("Category not found: " + categoryId, exception.getMessage());
            verify(categoryRepository).existsById(categoryId);
            verify(categoryRepository, never()).deleteById(categoryId);
        }
    }
}
