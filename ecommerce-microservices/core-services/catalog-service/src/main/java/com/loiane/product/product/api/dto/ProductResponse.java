package com.loiane.product.product.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Product information response")
public record ProductResponse(
        @Schema(description = "Product unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Product SKU", example = "LAPTOP-DELL-XPS13")
        String sku,
        @Schema(description = "Product name", example = "Dell XPS 13 Laptop")
        String name,
        @Schema(description = "Product URL slug", example = "dell-xps-13-laptop")
        String slug,
        @Schema(description = "Product brand", example = "Dell")
        String brand,
        @Schema(description = "Product detailed description")
        String description,
        @Schema(description = "Product status", example = "ACTIVE")
        String status,
        @Schema(description = "List of categories this product belongs to")
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
            String slug) {}
}
