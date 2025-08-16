package com.loiane.product.category.api.dto;

import com.loiane.product.common.validation.ValidSlug;
import com.loiane.product.common.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Request payload for creating or updating a category")
public record CategoryRequest(
        @Schema(description = "Category display name",
            example = "Electronics")
        @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @Size(max = 120, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        String name,

        @Schema(description = "URL-friendly category identifier",
            example = "electronics",
            pattern = "^[a-z0-9-]+$")
        @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @Size(max = 140, groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        @ValidSlug(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
        String slug,

        @Schema(description = "Parent category ID for hierarchical structure (null for root categories)",
            example = "550e8400-e29b-41d4-a716-446655440000")
        UUID parentId
) {}
