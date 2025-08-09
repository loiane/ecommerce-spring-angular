package com.loiane.product.category.api;

import com.loiane.product.category.CategoryService;
import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.dto.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Category management operations")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all categories", description = "Retrieve a paginated list of all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    })
    public Page<CategoryResponse> list(
            @Parameter(description = "Pagination information")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.listAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories", description = "Search categories by various criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    })
    public Page<CategoryResponse> search(
            @Parameter(description = "Category name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Category slug (partial match)")
            @RequestParam(required = false) String slug,
            @Parameter(description = "Parent category ID")
            @RequestParam(required = false) UUID parentId,
            @Parameter(description = "Filter root categories (true for categories without parent)")
            @RequestParam(required = false) Boolean isRoot,
            @Parameter(description = "Pagination information")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(name, slug, parentId, isRoot, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a single category by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved category"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public CategoryResponse get(
            @Parameter(description = "Category unique identifier")
            @PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a new category with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category data")
    })
    public ResponseEntity<CategoryResponse> create(
            @Parameter(description = "Category creation data")
            @Valid @RequestBody CategoryRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/categories/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Update an existing category with new information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category data"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public CategoryResponse update(
            @Parameter(description = "Category unique identifier")
            @PathVariable UUID id,
            @Parameter(description = "Category update data")
            @Valid @RequestBody CategoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Delete a category by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Category unique identifier")
            @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
