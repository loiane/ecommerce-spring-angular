package com.loiane.product.category;

import com.loiane.product.category.api.CategoryMapper;
import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.dto.CategoryResponse;
import com.loiane.product.common.exception.CategoryNotFoundException;
import com.loiane.product.common.exception.DuplicateCategorySlugException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

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
        log.debug("Fetching category with ID: {}", id);
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryMapper.toResponse(entity);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse create(CategoryRequest request) {
        log.debug("Creating category with slug: {}", request.slug());

        try {
            Category entity = CategoryMapper.toEntity(request);
            if (request.parentId() != null) {
                Category parent = categoryRepository.findById(request.parentId())
                        .orElseThrow(() -> new CategoryNotFoundException(request.parentId()));
                entity.setParent(parent);
            }
            Category saved = categoryRepository.save(entity);
            log.info("Successfully created category with ID: {} and slug: {}", saved.getId(), saved.getSlug());
            return CategoryMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create category with slug '{}' due to constraint violation: {}", request.slug(), e.getMessage(), e);
            throw new DuplicateCategorySlugException("Category creation failed for slug '" + request.slug() + "': " + e.getMessage(), e);
        }
    }

    @Transactional
    @CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
    public CategoryResponse update(UUID id, CategoryRequest request) {
        log.debug("Updating category with ID: {}", id);
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        CategoryMapper.updateEntity(entity, request);
        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.parentId()));
            entity.setParent(parent);
        } else {
            entity.setParent(null);
        }
        log.info("Successfully updated category with ID: {}", id);
        return CategoryMapper.toResponse(entity);
    }

    @Transactional
    @CacheEvict(value = {"categories", "categoryById"}, allEntries = true)
    public void delete(UUID id) {
        log.debug("Deleting category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
        log.info("Successfully deleted category with ID: {}", id);
    }

    private Specification<Category> addSpecification(Specification<Category> existing,
                                                    Specification<Category> newSpec) {
        return existing == null ? newSpec : existing.and(newSpec);
    }
}
