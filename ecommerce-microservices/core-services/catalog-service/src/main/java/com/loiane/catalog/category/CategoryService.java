package com.loiane.catalog.category;

import com.loiane.catalog.category.api.CategoryMapper;
import com.loiane.catalog.category.api.dto.CategoryRequest;
import com.loiane.catalog.category.api.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
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
    public List<CategoryResponse> listAll() {
        return CategoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID id) {
        var entity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
        return CategoryMapper.toResponse(entity);
    }

    @Transactional
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
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
