package com.loiane.product.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a requested product is not found in the system.
 * This is a business exception that maps to HTTP 404 Not Found status.
 */
@Schema(description = "Exception thrown when a product is not found")
public class ProductNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "PRODUCT_NOT_FOUND";
    private static final int HTTP_STATUS = HttpStatus.NOT_FOUND.value();

    @Schema(description = "Product ID that was not found")
    private final UUID productId;

    public ProductNotFoundException(UUID productId) {
        super(ERROR_CODE, "Product not found with ID: " + productId, HTTP_STATUS);
        this.productId = productId;
    }

    public ProductNotFoundException(String sku) {
        super(ERROR_CODE, "Product not found with SKU: " + sku, HTTP_STATUS);
        this.productId = null;
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, HTTP_STATUS, cause);
        this.productId = null;
    }

    public UUID getProductId() {
        return productId;
    }
}
