package com.loiane.catalog.product.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record ProductRequest(
        @NotBlank
        @Size(max = 64)
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must be uppercase letters, numbers and hyphens")
        String sku,

        @NotBlank
        @Size(max = 160)
        String name,

        @NotBlank
        @Size(max = 180)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase letters, numbers and hyphens")
        String slug,

        @Size(max = 120)
        String brand,

        String description,

        @NotBlank
        @Pattern(regexp = "^(ACTIVE|INACTIVE|DRAFT)$", message = "Invalid status")
        String status,

        Set<UUID> categoryIds
) {}
