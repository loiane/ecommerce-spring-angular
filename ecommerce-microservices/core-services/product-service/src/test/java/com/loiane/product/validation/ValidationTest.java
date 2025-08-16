package com.loiane.product.validation;

import com.loiane.product.common.validation.ValidationGroups;
import com.loiane.product.product.api.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom validation annotations and groups.
 */
class ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidSkuValidation() {
        // Valid SKU
        ProductRequest validRequest = new ProductRequest(
            "VALID-SKU-123",
            "Test Product",
            "test-product",
            "Apple",
            "Test description",
            "ACTIVE",
            Set.of()
        );

        Set<ConstraintViolation<ProductRequest>> violations =
            validator.validate(validRequest, ValidationGroups.Create.class);
        assertTrue(violations.isEmpty(), "Valid SKU should pass validation");

        // Invalid SKU (lowercase)
        ProductRequest invalidSkuRequest = new ProductRequest(
            "invalid-sku",
            "Test Product",
            "test-product",
            "Apple",
            "Test description",
            "ACTIVE",
            Set.of()
        );

        violations = validator.validate(invalidSkuRequest, ValidationGroups.Create.class);
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("sku")),
            "Invalid SKU should fail validation");
    }

    @Test
    void testValidStatusValidation() {
        // Invalid status
        ProductRequest invalidStatusRequest = new ProductRequest(
            "VALID-SKU-123",
            "Test Product",
            "test-product",
            "Apple",
            "Test description",
            "INVALID_STATUS",
            Set.of()
        );

        Set<ConstraintViolation<ProductRequest>> violations =
            validator.validate(invalidStatusRequest, ValidationGroups.Create.class);
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("status")),
            "Invalid status should fail validation");
    }

    @Test
    void testValidSlugValidation() {
        // Invalid slug (uppercase)
        ProductRequest invalidSlugRequest = new ProductRequest(
            "VALID-SKU-123",
            "Test Product",
            "INVALID-SLUG",
            "Apple",
            "Test description",
            "ACTIVE",
            Set.of()
        );

        Set<ConstraintViolation<ProductRequest>> violations =
            validator.validate(invalidSlugRequest, ValidationGroups.Create.class);
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("slug")),
            "Invalid slug should fail validation");
    }
}
