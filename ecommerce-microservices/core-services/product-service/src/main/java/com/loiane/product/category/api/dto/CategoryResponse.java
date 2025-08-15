package com.loiane.product.category.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        ParentSummary parent,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public record ParentSummary(UUID id, String name, String slug) {}
}
