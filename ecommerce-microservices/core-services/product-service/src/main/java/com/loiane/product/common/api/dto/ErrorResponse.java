package com.loiane.product.common.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Standard error response DTO for all API errors.
 * Provides consistent structure for error information across the application.
 */
@Schema(description = "Standard error response with consistent structure")
public record ErrorResponse(

        @Schema(description = "Timestamp when the error occurred",
                example = "2024-08-16T10:30:00Z")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime timestamp,

        @Schema(description = "HTTP status code",
                example = "404")
        int status,

        @Schema(description = "Business error code for programmatic handling",
                example = "PRODUCT_NOT_FOUND")
        String code,

        @Schema(description = "Human-readable error message",
                example = "Product not found with ID: 123e4567-e89b-12d3-a456-426614174000")
        String message,

        @Schema(description = "API path where the error occurred",
                example = "/api/products/123e4567-e89b-12d3-a456-426614174000")
        String path

) {
    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(
                OffsetDateTime.now(),
                status,
                code,
                message,
                path
        );
    }

    public static ErrorResponse of(int status, String code, String message) {
        return of(status, code, message, null);
    }
}
