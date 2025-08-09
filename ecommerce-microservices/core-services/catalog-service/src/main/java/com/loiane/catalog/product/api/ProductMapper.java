package com.loiane.catalog.product.api;

import com.loiane.catalog.product.Product;
import com.loiane.catalog.product.api.dto.ProductRequest;
import com.loiane.catalog.product.api.dto.ProductResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class ProductMapper {

    private ProductMapper() {}

    // Contract
    // - from request -> new entity (id null), caller sets categories & status validation
    // - update entity fields from request (except id, timestamps)
    // - to response with category summaries

    public static Product toEntity(ProductRequest req) {
        if (req == null) return null;
        Product p = new Product(req.sku(), req.name(), req.slug());
        p.setBrand(req.brand());
        p.setDescription(req.description());
        p.setStatus(req.status());
        return p;
    }

    public static void updateEntity(Product entity, ProductRequest req) {
        if (entity == null || req == null) return;
        entity.setSku(req.sku());
        entity.setName(req.name());
        entity.setSlug(req.slug());
        entity.setBrand(req.brand());
        entity.setDescription(req.description());
        entity.setStatus(req.status());
    }

    public static ProductResponse toResponse(Product entity) {
        if (entity == null) return null;
        List<ProductResponse.CategorySummary> cats = entity.getCategories() == null ? List.of() :
                entity.getCategories().stream()
                        .map(c -> new ProductResponse.CategorySummary(c.getId(), c.getName(), c.getSlug()))
                        .toList();
        return new ProductResponse(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getSlug(),
                entity.getBrand(),
                entity.getDescription(),
                entity.getStatus(),
                cats,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static Set<UUID> extractCategoryIds(ProductRequest req) {
        return req == null || req.categoryIds() == null ? Set.of() : req.categoryIds();
    }

    public static List<ProductResponse> toResponseList(List<Product> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream().map(ProductMapper::toResponse).toList();
    }
}
