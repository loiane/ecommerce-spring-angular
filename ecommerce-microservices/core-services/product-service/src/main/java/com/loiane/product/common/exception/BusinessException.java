package com.loiane.product.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Base exception class for all business logic exceptions in the product service.
 * Provides a consistent structure for error handling across the application.
 */
@Schema(description = "Base business exception with error code and message")
public abstract class BusinessException extends RuntimeException {

    @Schema(description = "Business error code", example = "PRODUCT_NOT_FOUND")
    private final String errorCode;

    @Schema(description = "HTTP status code", example = "404")
    private final int httpStatus;

    protected BusinessException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected BusinessException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
