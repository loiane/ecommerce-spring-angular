package com.loiane.product.product;

import com.loiane.product.category.Category;
import com.loiane.product.category.CategoryRepository;
import com.loiane.product.product.api.ProductMapper;
import com.loiane.product.product.api.dto.ProductRequest;
import com.loiane.product.product.api.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ProductResponse getById(UUID id) {
        var entity = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return ProductMapper.toResponse(entity);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        var entity = ProductMapper.toEntity(request);
        attachCategories(entity, request.categoryIds());
        var saved = productRepository.save(entity);
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        var entity = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        ProductMapper.updateEntity(entity, request);
        attachCategories(entity, request.categoryIds());
        return ProductMapper.toResponse(entity);
    }

    @Transactional
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
