package com.loiane.product.category;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class CategorySpecification {

    private static final String PARENT = "parent";

    private CategorySpecification() {}

    public static Specification<Category> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
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
        return (root, query, criteriaBuilder) -> {
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
        return (root, query, criteriaBuilder) -> {
            if (parentId == null) {
            return criteriaBuilder.equal(root.get(PARENT).get("id"), parentId);
            }
            return criteriaBuilder.equal(root.get("parent").get("id"), parentId);
        };
    }
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNull(root.get(PARENT));
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNull(root.get("parent"));
    }
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNotNull(root.get(PARENT));
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isNotNull(root.get("parent"));
    }
}
