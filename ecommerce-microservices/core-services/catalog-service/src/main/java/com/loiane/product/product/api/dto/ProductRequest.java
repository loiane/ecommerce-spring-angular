package com.loiane.product.product.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Product creation/update request")
public record ProductRequest(
        @Schema(description = "Product SKU - unique identifier", example = "LAPTOP-DELL-XPS13")
        @NotBlank
        @Size(max = 64)
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must be uppercase letters, numbers and hyphens")
        String sku,

        @Schema(description = "Product name", example = "Dell XPS 13 Laptop")
        @NotBlank
        @Size(max = 160)
        String name,

        @Schema(description = "Product URL slug", example = "dell-xps-13-laptop")
        @NotBlank
        @Size(max = 180)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase letters, numbers and hyphens")
        String slug,

        @Schema(description = "Product brand", example = "Dell")
        @Size(max = 120)
        String brand,

        @Schema(description = "Product detailed description", example = "High-performance ultrabook with Intel Core i7 processor")
        String description,

        @Schema(description = "Product status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DRAFT"})
        @NotBlank
        @Pattern(regexp = "^(ACTIVE|INACTIVE|DRAFT)$", message = "Invalid status")
        String status,

        @Schema(description = "Set of category IDs this product belongs to")
        Set<UUID> categoryIds
) {}
