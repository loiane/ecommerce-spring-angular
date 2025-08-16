package com.loiane.product.category.api;

import com.loiane.product.category.CategoryService;
import com.loiane.product.category.api.dto.CategoryRequest;
import com.loiane.product.category.api.dto.CategoryResponse;
import com.loiane.product.common.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for category management operations.
 *
 * @author Loiane Groner
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Category management operations")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
        summary = "List all categories",
        description = "Retrieve a paginated list of all categories in the system."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    public Page<CategoryResponse> list(
            @Parameter(description = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.listAll(pageable);
    }

    @GetMapping("/search")
    @Operation(
        summary = "Advanced category search",
        description = """
            Search categories with multiple filtering options:
            - **name**: Case-insensitive partial match on category name
            - **slug**: Exact or partial match on category slug
            - **parentId**: Filter by parent category ID (for hierarchical navigation)
            - **isRoot**: Filter for root categories (true) or child categories (false)

            All filters can be combined for complex category queries.
            """
    )
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    public Page<CategoryResponse> search(
            @Parameter(description = "Category name (partial, case-insensitive)",
                example = "Electronics")
            @RequestParam(required = false) String name,

            @Parameter(description = "Category slug (URL-friendly identifier)",
                example = "electronics")
            @RequestParam(required = false) String slug,

            @Parameter(description = "Parent category ID for hierarchy filtering",
                example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID parentId,

            @Parameter(description = "Filter for root categories (true) or child categories (false)")
            @RequestParam(required = false) Boolean isRoot,

            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(name, slug, parentId, isRoot, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get category by ID",
        description = "Retrieve a specific category by its unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Category found successfully")
    @ApiResponse(responseCode = "400", description = "Invalid category ID format")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public CategoryResponse get(
            @Parameter(description = "Category unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(
        summary = "Create a new category",
        description = "Create a new category with the provided information."
    )
    @ApiResponse(responseCode = "201", description = "Category created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CategoryResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid category data")
    @ApiResponse(responseCode = "409", description = "Category with same slug already exists")
    public ResponseEntity<CategoryResponse> create(
            @Parameter(description = "Category creation data", required = true)
            @Validated(ValidationGroups.Create.class) @RequestBody CategoryRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/categories/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update existing category",
        description = "Update an existing category with new information."
    )
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid category data or ID format")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "409", description = "Category with same slug already exists")
    public CategoryResponse update(
            @Parameter(description = "Category unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,

            @Parameter(description = "Updated category data", required = true)
            @Validated(ValidationGroups.Update.class) @RequestBody CategoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete category",
        description = "Delete an existing category from the system. Note: Categories with child categories or associated products cannot be deleted."
    )
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid category ID format")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "409", description = "Cannot delete category with child categories or associated products")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Category unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
