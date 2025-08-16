package com.loiane.product.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a requested category is not found in the system.
 * This is a business exception that maps to HTTP 404 Not Found status.
 */
@Schema(description = "Exception thrown when a category is not found")
public class CategoryNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "CATEGORY_NOT_FOUND";
    private static final int HTTP_STATUS = HttpStatus.NOT_FOUND.value();

    @Schema(description = "Category ID that was not found")
    private final UUID categoryId;

    public CategoryNotFoundException(UUID categoryId) {
        super(ERROR_CODE, "Category not found with ID: " + categoryId, HTTP_STATUS);
        this.categoryId = categoryId;
    }

    public CategoryNotFoundException(String slug) {
        super(ERROR_CODE, "Category not found with slug: " + slug, HTTP_STATUS);
        this.categoryId = null;
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, HTTP_STATUS, cause);
        this.categoryId = null;
    }

    public UUID getCategoryId() {
        return categoryId;
    }
}
