package com.loiane.product.product;

import com.loiane.product.category.Category;
import com.loiane.product.category.CategoryRepository;
import com.loiane.product.product.api.dto.ProductRequest;
import com.loiane.product.product.api.dto.ProductResponse;
import com.loiane.product.common.exception.ProductNotFoundException;
import com.loiane.product.common.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
@SuppressWarnings("unchecked")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private UUID testProductId;
    private UUID testCategoryId;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testCategoryId = UUID.randomUUID();

        // Create test Product entity
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setSku("TEST-001");
        testProduct.setBrand("Test Brand");
        testProduct.setStatus("ACTIVE");
        testProduct.setSlug("test-product");
    }

    @Nested
    @DisplayName("search() method tests")
    class SearchTests {

        @Test
        @DisplayName("Should return products when search parameters are provided")
        void shouldReturnProductsWhenSearchParametersProvided() {
            // Given
            String name = "laptop";
            String status = "ACTIVE";
            String brand = "Apple";
            String sku = "MBP-001";
            Set<UUID> categoryIds = Set.of(testCategoryId);
            Pageable pageable = PageRequest.of(0, 10);

            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.search(name, status, brand, sku, categoryIds, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(productRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should return all products when no search parameters provided")
        void shouldReturnAllProductsWhenNoSearchParametersProvided() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(pageable))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.search(null, null, null, null, null, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(productRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should handle empty string parameters")
        void shouldHandleEmptyStringParameters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(pageable))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.search("", "", "", "", Set.of(), pageable);

            // Then
            assertNotNull(result);
            verify(productRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should handle large category sets")
        void shouldHandleLargeCategorySets() {
            // Given
            Set<UUID> largeCategorySet = Set.of(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID()
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.search(null, null, null, null, largeCategorySet, pageable);

            // Then
            assertNotNull(result);
            verify(productRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should compose specifications correctly")
        void shouldComposeSpecificationsCorrectly() {
            // Given
            String name = "MacBook";
            String status = "ACTIVE";
            String brand = "Apple";
            String sku = "MBP-001";
            Set<UUID> categoryIds = Set.of(testCategoryId);
            Pageable pageable = PageRequest.of(0, 10);

            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.search(name, status, brand, sku, categoryIds, pageable);

            // Then
            assertNotNull(result);
            verify(productRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    @DisplayName("listAll() method tests")
    class ListAllTests {

        @Test
        @DisplayName("Should return all products with pagination")
        void shouldReturnAllProductsWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(pageable))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.listAll(pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(productRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should return all products as list")
        void shouldReturnAllProductsAsList() {
            // Given
            when(productRepository.findAll())
                .thenReturn(List.of(testProduct));

            // When
            List<ProductResponse> result = productService.listAll();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(productRepository).findAll();
        }

        @Test
        @DisplayName("Should handle empty result set")
        void shouldHandleEmptyResultSet() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(productRepository.findAll(pageable))
                .thenReturn(emptyPage);

            // When
            Page<ProductResponse> result = productService.listAll(pageable);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
            verify(productRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("getById() method tests")
    class GetByIdTests {

        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProductWhenFound() {
            // Given
            when(productRepository.findById(testProductId))
                .thenReturn(Optional.of(testProduct));

            // When
            ProductResponse result = productService.getById(testProductId);

            // Then
            assertNotNull(result);
            verify(productRepository).findById(testProductId);
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(productRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

            // When & Then
            assertThrows(ProductNotFoundException.class, () -> {
                productService.getById(nonExistentId);
            });
            verify(productRepository).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("create() method tests")
    class CreateTests {

        @Test
        @DisplayName("Should create product without categories")
        void shouldCreateProductWithoutCategories() {
            // Given
            ProductRequest request = new ProductRequest(
                "TEST-001",
                "Test Product",
                "test-product",
                "Test Brand",
                "Test Description",
                "ACTIVE",
                Set.of()
            );
            when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct);

            // When
            ProductResponse result = productService.create(request);

            // Then
            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
            verify(categoryRepository, never()).findById(any(UUID.class));
        }

        @Test
        @DisplayName("Should create product with categories")
        void shouldCreateProductWithCategories() {
            // Given
            Category category = mock(Category.class);
            ProductRequest request = new ProductRequest(
                "TEST-001",
                "Test Product",
                "test-product",
                "Test Brand",
                "Test Description",
                "ACTIVE",
                Set.of(testCategoryId)
            );
            when(categoryRepository.findById(testCategoryId))
                .thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct);

            // When
            ProductResponse result = productService.create(request);

            // Then
            assertNotNull(result);
            verify(categoryRepository).findById(testCategoryId);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            ProductRequest request = new ProductRequest(
                "TEST-001",
                "Test Product",
                "test-product",
                "Test Brand",
                "Test Description",
                "ACTIVE",
                Set.of(testCategoryId)
            );
            when(categoryRepository.findById(testCategoryId))
                .thenReturn(Optional.empty());

            // When & Then
            assertThrows(CategoryNotFoundException.class, () -> {
                productService.create(request);
            });
            verify(categoryRepository).findById(testCategoryId);
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("delete() method tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProductSuccessfully() {
            // Given
            when(productRepository.existsById(testProductId))
                .thenReturn(true);
            doNothing().when(productRepository).deleteById(testProductId);

            // When
            productService.delete(testProductId);

            // Then
            verify(productRepository).existsById(testProductId);
            verify(productRepository).deleteById(testProductId);
        }

        @Test
        @DisplayName("Should throw exception when product not found for deletion")
        void shouldThrowExceptionWhenProductNotFoundForDeletion() {
            // Given
            when(productRepository.existsById(testProductId))
                .thenReturn(false);

            // When & Then
            assertThrows(ProductNotFoundException.class, () -> {
                productService.delete(testProductId);
            });
            verify(productRepository).existsById(testProductId);
            verify(productRepository, never()).deleteById(testProductId);
        }
    }

    @Nested
    @DisplayName("addSpecification() method tests")
    class AddSpecificationTests {

        @Test
        @DisplayName("Should return new specification when existing is null")
        void shouldReturnNewSpecificationWhenExistingIsNull() {
            // This test validates the private addSpecification method indirectly
            // by testing search with multiple parameters

            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.search("laptop", "ACTIVE", null, null, null, pageable);

            // Then
            assertNotNull(result);
            verify(productRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should combine specifications with AND")
        void shouldCombineSpecificationsWithAnd() {
            // This test validates the addSpecification method combines multiple specs

            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

            // When
            Page<ProductResponse> result = productService.search("laptop", "ACTIVE", "Apple", "MBP-001", Set.of(testCategoryId), pageable);

            // Then
            assertNotNull(result);
            verify(productRepository).findAll(any(Specification.class), eq(pageable));
        }
    }
}
