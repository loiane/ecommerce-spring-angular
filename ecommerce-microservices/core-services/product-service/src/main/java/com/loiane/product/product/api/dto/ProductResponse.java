package com.loiane.product.product.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Product information response")
public record ProductResponse(
        @Schema(description = "Unique product identifier",
            example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Product SKU",
            example = "IPH-15-PRO-256")
        String sku,

        @Schema(description = "Product display name",
            example = "iPhone 15 Pro")
        String name,

        @Schema(description = "URL-friendly identifier",
            example = "iphone-15-pro")
        String slug,

        @Schema(description = "Product brand",
            example = "Apple")
        String brand,

        @Schema(description = "Product description",
            example = "Latest iPhone with advanced camera system")
        String description,

        @Schema(description = "Product status",
            example = "ACTIVE")
        String status,

        @Schema(description = "Categories this product belongs to")
        List<ProductResponse.CategorySummary> categories,

        @Schema(description = "Product creation timestamp")
        OffsetDateTime createdAt,

        @Schema(description = "Product last update timestamp")
        OffsetDateTime updatedAt
) {
    @Schema(description = "Category summary information")
    public record CategorySummary(
        @Schema(description = "Category unique identifier")
        UUID id,

        @Schema(description = "Category name")
        String name,

        @Schema(description = "Category URL slug")
        String slug
    ) {}
}
