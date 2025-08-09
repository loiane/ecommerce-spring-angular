package com.loiane.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loiane.product.category.Category;
import com.loiane.product.category.CategoryRepository;
import com.loiane.product.common.BaseIntegrationTest;
import com.loiane.product.product.Product;
import com.loiane.product.product.ProductRepository;
import com.loiane.product.product.api.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@BaseIntegrationTest
@DisplayName("Product-Category Relationship Integration Tests")
class ProductCategoryRelationshipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronics;
    private Category smartphones;
    private Category laptops;

    @BeforeEach
    void setUp() {
        // Clean up data before each test
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create category hierarchy
        electronics = new Category("Electronics", "electronics");
        electronics = categoryRepository.save(electronics);

        smartphones = new Category("Smartphones", "smartphones");
        smartphones.setParent(electronics);
        smartphones = categoryRepository.save(smartphones);

        laptops = new Category("Laptops", "laptops");
        laptops.setParent(electronics);
        laptops = categoryRepository.save(laptops);
    }

    @Test
    @DisplayName("Should create product with multiple categories and verify relationships")
    void shouldCreateProductWithMultipleCategoriesAndVerifyRelationships() throws Exception {
        // Create a product with multiple categories
        var productRequest = new ProductRequest(
            "IP15-PRO-256",
            "iPhone 15 Pro",
            "iphone-15-pro",
            "Apple",
            "Professional smartphone with advanced features",
            "ACTIVE",
            Set.of(electronics.getId(), smartphones.getId())
        );

        var response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is("iPhone 15 Pro")))
            .andExpect(jsonPath("$.categories", hasSize(2)))
            .andReturn();

        // Extract product ID from response
        var productJson = objectMapper.readTree(response.getResponse().getContentAsString());
        var productId = productJson.get("id").asText();

        // Verify the product-category relationships in database
        var savedProduct = productRepository.findById(java.util.UUID.fromString(productId)).orElseThrow();
        assertThat(savedProduct.getCategories()).hasSize(2);
        assertThat(savedProduct.getCategories())
            .extracting(Category::getName)
            .containsExactlyInAnyOrder("Electronics", "Smartphones");
    }

    @Test
    @DisplayName("Should search products by category and verify results")
    void shouldSearchProductsByCategoryAndVerifyResults() throws Exception {
        // Create products in different categories
        var iphone = new Product("IP15-128", "iPhone 15", "iphone-15");
        iphone.setBrand("Apple");
        iphone.getCategories().add(smartphones);
        productRepository.save(iphone);

        var macbook = new Product("MBP-M3", "MacBook Pro M3", "macbook-pro-m3");
        macbook.setBrand("Apple");
        macbook.getCategories().add(laptops);
        productRepository.save(macbook);

        var genericPhone = new Product("GP-001", "Generic Phone", "generic-phone");
        genericPhone.setBrand("Generic");
        genericPhone.getCategories().add(smartphones);
        productRepository.save(genericPhone);

        // Search for products in smartphones category
        mockMvc.perform(get("/api/products/search")
                .param("categoryIds", smartphones.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("iPhone 15", "Generic Phone")))
            .andExpect(jsonPath("$.content[*].categories[0].name", everyItem(is("Smartphones"))));

        // Search for products in laptops category
        mockMvc.perform(get("/api/products/search")
                .param("categoryIds", laptops.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name", is("MacBook Pro M3")))
            .andExpect(jsonPath("$.content[0].categories[0].name", is("Laptops")));

        // Search for products in parent electronics category
        mockMvc.perform(get("/api/products/search")
                .param("categoryIds", electronics.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0))); // No products directly in electronics
    }

    @Test
    @DisplayName("Should update product categories and verify changes")
    void shouldUpdateProductCategoriesAndVerifyChanges() throws Exception {
        // Create a product with one category
        var product = new Product("LAPTOP-001", "Gaming Laptop", "gaming-laptop");
        product.setBrand("ASUS");
        product.getCategories().add(laptops);
        product = productRepository.save(product);

        // Update product to be in multiple categories
        var updateRequest = new ProductRequest(
            "LAPTOP-001-UPDATED",
            "Gaming Laptop Updated",
            "gaming-laptop-updated",
            "ASUS ROG",
            "High-performance gaming laptop",
            "ACTIVE",
            Set.of(electronics.getId(), laptops.getId())
        );

        mockMvc.perform(put("/api/products/{id}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Gaming Laptop Updated")))
            .andExpect(jsonPath("$.categories", hasSize(2)));

        // Verify the updated relationships in database
        var updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getCategories()).hasSize(2);
        assertThat(updatedProduct.getCategories())
            .extracting(Category::getName)
            .containsExactlyInAnyOrder("Electronics", "Laptops");
    }

    @Test
    @DisplayName("Should handle category hierarchy in search results")
    void shouldHandleCategoryHierarchyInSearchResults() throws Exception {
        // Create products at different levels of the hierarchy
        var electronicsProduct = new Product("ELEC-001", "Electronic Device", "electronic-device");
        electronicsProduct.getCategories().add(electronics);
        productRepository.save(electronicsProduct);

        var smartphoneProduct = new Product("PHONE-001", "Smart Phone", "smart-phone");
        smartphoneProduct.getCategories().add(smartphones);
        productRepository.save(smartphoneProduct);

        // Search in parent category should not return child category products
        mockMvc.perform(get("/api/products/search")
                .param("categoryIds", electronics.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name", is("Electronic Device")));

        // Search in child category should only return its products
        mockMvc.perform(get("/api/products/search")
                .param("categoryIds", smartphones.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name", is("Smart Phone")));

        // Search with multiple category IDs
        mockMvc.perform(get("/api/products/search")
                .param("categoryIds", electronics.getId() + "," + smartphones.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Electronic Device", "Smart Phone")));
    }
}