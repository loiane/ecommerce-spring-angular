package com.loiane.product.category.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Category information response")
public record CategoryResponse(
        @Schema(description = "Unique category identifier",
            example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Category display name",
            example = "Electronics")
        String name,

        @Schema(description = "URL-friendly identifier",
            example = "electronics")
        String slug,

        @Schema(description = "Parent category information (null for root categories)")
        ParentSummary parent,

        @Schema(description = "Category creation timestamp")
        OffsetDateTime createdAt,

        @Schema(description = "Category last update timestamp")
        OffsetDateTime updatedAt
) {
    @Schema(description = "Parent category summary information")
    public record ParentSummary(
        @Schema(description = "Parent category unique identifier")
        UUID id,

        @Schema(description = "Parent category name")
        String name,

        @Schema(description = "Parent category URL slug")
        String slug
    ) {}
}
