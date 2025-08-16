package com.loiane.product.product.api.dto;

import com.loiane.product.common.validation.ValidSku;
import com.loiane.product.common.validation.ValidSlug;
import com.loiane.product.common.validation.ValidStatus;
import com.loiane.product.common.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Request payload for creating or updating a product")
public record ProductRequest(
        @Schema(description = "Unique product identifier (Stock Keeping Unit)",
            example = "IPH-15-PRO-256",
            pattern = "^[A-Z0-9-]+$")
        @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @Size(max = 64, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @ValidSku(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        String sku,

        @Schema(description = "Product display name",
            example = "iPhone 15 Pro")
        @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @Size(max = 160, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        String name,

        @Schema(description = "URL-friendly product identifier",
            example = "iphone-15-pro",
            pattern = "^[a-z0-9-]+$")
        @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @Size(max = 180, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @ValidSlug(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        String slug,

        @Schema(description = "Product brand or manufacturer",
            example = "Apple")
        @Size(max = 120, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        String brand,

        @Schema(description = "Detailed product description",
            example = "Latest iPhone with advanced camera system and A17 Pro chip")
        String description,

        @Schema(description = "Current product status",
            allowableValues = {"ACTIVE", "INACTIVE", "DRAFT"},
            example = "ACTIVE")
        @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @ValidStatus(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        String status,

        @Schema(description = "Set of category IDs this product belongs to",
            example = "['550e8400-e29b-41d4-a716-446655440000']")
        Set<UUID> categoryIds
) {}
