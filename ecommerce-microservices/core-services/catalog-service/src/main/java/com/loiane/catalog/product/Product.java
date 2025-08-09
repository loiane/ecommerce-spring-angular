package com.loiane.catalog.product;

import com.loiane.catalog.category.Category;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sku", nullable = false, length = 64, unique = true)
    private String sku;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "slug", nullable = false, length = 180)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "brand", length = 120)
    private String brand;

    @Column(name = "status", nullable = false, length = 40)
    private String status = "ACTIVE";

    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Product() {
        // JPA
    }

    public Product(String sku, String name, String slug) {
        this.sku = Objects.requireNonNull(sku);
        this.name = Objects.requireNonNull(name);
        this.slug = Objects.requireNonNull(slug);
    }

    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and minimal setters
    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getBrand() { return brand; }
    public String getStatus() { return status; }
    public Set<Category> getCategories() { return categories; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setSku(String sku) { this.sku = Objects.requireNonNull(sku); }
    public void setName(String name) { this.name = Objects.requireNonNull(name); }
    public void setSlug(String slug) { this.slug = Objects.requireNonNull(slug); }
    public void setDescription(String description) { this.description = description; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setStatus(String status) { this.status = Objects.requireNonNull(status); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
