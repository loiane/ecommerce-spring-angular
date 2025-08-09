package com.loiane.product.product;

import com.loiane.product.category.Category;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public final class ProductSpecification {

    private static final String STATUS = "status";

    private ProductSpecification() {}

    public static Specification<Product> hasName(String name) {
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

    public static Specification<Product> hasStatus(String status) {
        return (root, _, criteriaBuilder) -> {
            if (status == null || status.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get(STATUS), status);
        };
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, _, criteriaBuilder) -> {
            if (brand == null || brand.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("brand")),
                "%" + brand.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasSku(String sku) {
        return (root, _, criteriaBuilder) -> {
            if (sku == null || sku.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("sku")),
                "%" + sku.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasCategory(UUID categoryId) {
        return (root, _, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, Category> categoryJoin = root.join("categories", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    public static Specification<Product> hasAnyCategory(Set<UUID> categoryIds) {
        return (root, _, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, Category> categoryJoin = root.join("categories", JoinType.INNER);
            return categoryJoin.get("id").in(categoryIds);
        };
    }
}
