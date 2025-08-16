package com.loiane.product.product;

import com.loiane.product.category.Category;
import com.loiane.product.category.CategoryRepository;
import com.loiane.product.product.api.ProductMapper;
import com.loiane.product.product.api.dto.ProductRequest;
import com.loiane.product.product.api.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> listAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listAll() {
        return ProductMapper.toResponseList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String name, String status, String brand, String sku,
                                       Set<UUID> categoryIds, Pageable pageable) {
        Specification<Product> spec = null;

        if (isNotEmpty(name)) {
            spec = addSpecification(spec, ProductSpecification.hasName(name));
        }
        if (isNotEmpty(status)) {
            spec = addSpecification(spec, ProductSpecification.hasStatus(status));
        }
        if (isNotEmpty(brand)) {
            spec = addSpecification(spec, ProductSpecification.hasBrand(brand));
        }
        if (isNotEmpty(sku)) {
            spec = addSpecification(spec, ProductSpecification.hasSku(sku));
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = addSpecification(spec, ProductSpecification.hasAnyCategory(categoryIds));
        }

        if (spec == null) {
            return productRepository.findAll(pageable).map(ProductMapper::toResponse);
        }
        return productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private Specification<Product> addSpecification(Specification<Product> existing,
                                                   Specification<Product> newSpec) {
        return existing == null ? newSpec : existing.and(newSpec);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productById", key = "#id")
    public ProductResponse getById(UUID id) {
        var entity = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return ProductMapper.toResponse(entity);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse create(ProductRequest request) {
        var entity = ProductMapper.toEntity(request);
        attachCategories(entity, request.categoryIds());
        var saved = productRepository.save(entity);
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public ProductResponse update(UUID id, ProductRequest request) {
        var entity = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        ProductMapper.updateEntity(entity, request);
        attachCategories(entity, request.categoryIds());
        var saved = productRepository.save(entity);
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    private void attachCategories(Product entity, Set<UUID> categoryIds) {
        entity.getCategories().clear();
        if (categoryIds == null || categoryIds.isEmpty()) return;
        for (UUID cid : categoryIds) {
            Category category = categoryRepository.findById(cid)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + cid));
            entity.getCategories().add(category);
        }
    }
}
