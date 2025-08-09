package com.loiane.product.product.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String slug,
        String brand,
        String description,
        String status,
        List<ProductResponse.CategorySummary> categories,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public record CategorySummary(UUID id, String name, String slug) {}
}
