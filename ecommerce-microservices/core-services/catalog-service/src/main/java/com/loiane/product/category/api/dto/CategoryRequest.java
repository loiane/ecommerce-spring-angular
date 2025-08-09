package com.loiane.product.category.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Category creation/update request")
public record CategoryRequest(
        @Schema(description = "Category name", example = "Electronics")
        @NotBlank @Size(max = 120) String name,
        @Schema(description = "Category URL slug", example = "electronics")
        @NotBlank @Size(max = 140)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase letters, numbers and hyphens")
        String slug,
        @Schema(description = "Parent category ID (null for root categories)")
        UUID parentId
) {}
