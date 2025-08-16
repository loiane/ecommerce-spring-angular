package com.loiane.product.category.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Request payload for creating or updating a category")
public record CategoryRequest(
        @Schema(description = "Category display name",
            example = "Electronics")
        @NotBlank
        @Size(max = 120)
        String name,

        @Schema(description = "URL-friendly category identifier",
            example = "electronics",
            pattern = "^[a-z0-9-]+$")
        @NotBlank
        @Size(max = 140)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase letters, numbers and hyphens")
        String slug,

        @Schema(description = "Parent category ID for hierarchical structure (null for root categories)",
            example = "550e8400-e29b-41d4-a716-446655440000")
        UUID parentId
) {}
