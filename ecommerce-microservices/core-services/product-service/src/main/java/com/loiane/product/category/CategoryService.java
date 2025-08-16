package com.loiane.product.category;

import com.loiane.product.category.api.CategoryMapper;
import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> listAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public List<CategoryResponse> listAll() {
        return CategoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> search(String name, String slug, UUID parentId,
                                        Boolean isRoot, Pageable pageable) {
        Specification<Category> spec = null;

        if (name != null && !name.trim().isEmpty()) {
            spec = addSpecification(spec, CategorySpecification.hasName(name));
        }
        if (slug != null && !slug.trim().isEmpty()) {
            spec = addSpecification(spec, CategorySpecification.hasSlug(slug));
        }
        if (parentId != null) {
            spec = addSpecification(spec, CategorySpecification.hasParent(parentId));
        }
        if (isRoot != null && isRoot) {
            spec = addSpecification(spec, CategorySpecification.isRootCategory());
        }
        if (isRoot != null && !isRoot) {
            spec = addSpecification(spec, CategorySpecification.hasSubCategories());
        }

        if (spec == null) {
            return categoryRepository.findAll(pageable).map(CategoryMapper::toResponse);
        }
        return categoryRepository.findAll(spec, pageable).map(CategoryMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryById", key = "#id")
    public CategoryResponse getById(UUID id) {
        var entity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        return CategoryMapper.toResponse(entity);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse create(CategoryRequest request) {
        var entity = CategoryMapper.toEntity(request);
        if (request.parentId() != null) {
            var parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found: " + request.parentId()));
            entity.setParent(parent);
        }
        var saved = categoryRepository.save(entity);
        return CategoryMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
    public CategoryResponse update(UUID id, CategoryRequest request) {
        var entity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        CategoryMapper.updateEntity(entity, request);
        if (request.parentId() != null) {
            var parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found: " + request.parentId()));
            entity.setParent(parent);
        } else {
            entity.setParent(null);
        }
        return CategoryMapper.toResponse(entity);
    }

    @Transactional
    @CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private Specification<Category> addSpecification(Specification<Category> existing,
                                                    Specification<Category> newSpec) {
        return existing == null ? newSpec : existing.and(newSpec);
    }
}
