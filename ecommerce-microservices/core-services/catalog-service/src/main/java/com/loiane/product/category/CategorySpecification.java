package com.loiane.product.category;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class CategorySpecification {

    private static final String PARENT = "parent";

    private CategorySpecification() {}

    public static Specification<Category> hasName(String name) {
        return (root, _, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Category> hasSlug(String slug) {
        return (root, _, criteriaBuilder) -> {
            if (slug == null || slug.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("slug")),
                "%" + slug.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Category> hasParent(UUID parentId) {
        return (root, _, criteriaBuilder) -> {
            if (parentId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(PARENT).get("id"), parentId);
        };
    }

    public static Specification<Category> isRootCategory() {
        return (root, _, criteriaBuilder) ->
            criteriaBuilder.isNull(root.get(PARENT));
    }

    public static Specification<Category> hasSubCategories() {
        return (root, _, criteriaBuilder) ->
            criteriaBuilder.isNotNull(root.get("subCategories"));
    }
}
