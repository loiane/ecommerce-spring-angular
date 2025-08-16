package com.loiane.product.common.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Validation error response DTO for handling validation failures.
 * Extends the standard error response with field-specific validation errors.
 */
@Schema(description = "Validation error response with field-specific errors")
public record ValidationErrorResponse(

        @Schema(description = "Timestamp when the error occurred",
                example = "2024-08-16T10:30:00Z")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime timestamp,

        @Schema(description = "HTTP status code",
                example = "400")
        int status,

        @Schema(description = "Business error code for programmatic handling",
                example = "VALIDATION_ERROR")
        String code,

        @Schema(description = "General error message",
                example = "Input validation failed")
        String message,

        @Schema(description = "API path where the error occurred",
                example = "/api/products")
        String path,

        @Schema(description = "Map of field names to their specific error messages",
                example = "{\"sku\": \"SKU is required\", \"name\": \"Name cannot be blank\"}")
        Map<String, String> errors

) {
    public static ValidationErrorResponse of(int status, String code, String message, String path, Map<String, String> errors) {
        return new ValidationErrorResponse(
                OffsetDateTime.now(),
                status,
                code,
                message,
                path,
                errors
        );
    }

    public static ValidationErrorResponse of(int status, String code, String message, Map<String, String> errors) {
        return of(status, code, message, null, errors);
    }
}
