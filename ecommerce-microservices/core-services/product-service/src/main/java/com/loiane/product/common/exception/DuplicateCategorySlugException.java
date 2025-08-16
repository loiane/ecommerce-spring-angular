package com.loiane.product.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a category with a slug that already exists.
 * This is a business exception that maps to HTTP 409 Conflict status.
 */
@Schema(description = "Exception thrown when a category slug already exists")
public class DuplicateCategorySlugException extends BusinessException {

    private static final String ERROR_CODE = "DUPLICATE_CATEGORY_SLUG";
    private static final int HTTP_STATUS = HttpStatus.CONFLICT.value();

    @Schema(description = "Slug that already exists")
    private final String slug;

    public DuplicateCategorySlugException(String slug) {
        super(ERROR_CODE, "Category with slug already exists: " + slug, HTTP_STATUS);
        this.slug = slug;
    }

    public DuplicateCategorySlugException(String slug, Throwable cause) {
        super(ERROR_CODE, "Category with slug already exists: " + slug, HTTP_STATUS, cause);
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }
}
