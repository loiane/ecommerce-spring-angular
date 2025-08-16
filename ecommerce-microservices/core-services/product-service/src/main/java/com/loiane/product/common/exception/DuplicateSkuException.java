package com.loiane.product.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a product with a SKU that already exists.
 * This is a business exception that maps to HTTP 409 Conflict status.
 */
@Schema(description = "Exception thrown when a SKU already exists")
public class DuplicateSkuException extends BusinessException {

    private static final String ERROR_CODE = "DUPLICATE_SKU";
    private static final int HTTP_STATUS = HttpStatus.CONFLICT.value();

    @Schema(description = "SKU that already exists")
    private final String sku;

    public DuplicateSkuException(String sku) {
        super(ERROR_CODE, "Product with SKU already exists: " + sku, HTTP_STATUS);
        this.sku = sku;
    }

    public DuplicateSkuException(String sku, Throwable cause) {
        super(ERROR_CODE, "Product with SKU already exists: " + sku, HTTP_STATUS, cause);
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }
}
