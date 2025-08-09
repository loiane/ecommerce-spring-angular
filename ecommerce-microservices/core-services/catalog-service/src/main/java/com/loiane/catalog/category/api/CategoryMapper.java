package com.loiane.catalog.category.api;

import com.loiane.catalog.category.Category;
import com.loiane.catalog.category.api.dto.CategoryRequest;
import com.loiane.catalog.category.api.dto.CategoryResponse;

import java.util.List;

public final class CategoryMapper {

    private CategoryMapper() {}

    public static Category toEntity(CategoryRequest req) {
        if (req == null) return null;
    return new Category(req.name(), req.slug());
    }

    public static void updateEntity(Category entity, CategoryRequest req) {
        if (entity == null || req == null) return;
        entity.setName(req.name());
        entity.setSlug(req.slug());
    }

    public static CategoryResponse toResponse(Category entity) {
        if (entity == null) return null;
        CategoryResponse.ParentSummary parent = null;
        if (entity.getParent() != null) {
            parent = new CategoryResponse.ParentSummary(
                    entity.getParent().getId(),
                    entity.getParent().getName(),
                    entity.getParent().getSlug()
            );
        }
        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                parent,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static List<CategoryResponse> toResponseList(List<Category> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream().map(CategoryMapper::toResponse).toList();
    }
}
