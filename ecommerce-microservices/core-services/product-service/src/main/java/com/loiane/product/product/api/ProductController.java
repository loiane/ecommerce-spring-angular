package com.loiane.product.product.api;

import com.loiane.product.common.validation.ValidationGroups;
import com.loiane.product.common.validation.ValidStatus;
import com.loiane.product.product.ProductService;
import com.loiane.product.product.api.dto.ProductRequest;
import com.loiane.product.product.api.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

/**
 * REST controller for product management operations.
 *
 * @author Loiane Groner
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management operations")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
        summary = "List all products",
        description = "Retrieve a paginated list of all products in the system."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    public Page<ProductResponse> list(
            @Parameter(description = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.listAll(pageable);
    }

    @GetMapping("/search")
    @Operation(
        summary = "Advanced product search",
        description = """
            Search products with multiple filtering options:
            - **name**: Case-insensitive partial match on product name
            - **status**: Exact match on product status (ACTIVE, DRAFT, DISCONTINUED)
            - **brand**: Case-insensitive partial match on brand name
            - **sku**: Partial match on product SKU
            - **categoryIds**: Filter by one or more category IDs

            All filters can be combined. Results are paginated and sortable.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
        }
    )
    public Page<ProductResponse> search(
            @Parameter(description = "Product name (partial, case-insensitive)",
                example = "iPhone")
            @RequestParam(required = false) String name,

            @Parameter(description = "Product status",
                schema = @Schema(allowableValues = {"ACTIVE", "DRAFT", "DISCONTINUED"}),
                example = "ACTIVE")
            @ValidStatus(groups = ValidationGroups.Search.class)
            @RequestParam(required = false) String status,

            @Parameter(description = "Brand name (partial, case-insensitive)",
                example = "Apple")
            @RequestParam(required = false) String brand,

            @Parameter(description = "Product SKU (partial match)",
                example = "IPH-15")
            @RequestParam(required = false) String sku,

            @Parameter(description = "Category IDs to filter by",
                example = "['550e8400-e29b-41d4-a716-446655440000']")
            @RequestParam(required = false) Set<UUID> categoryIds,

            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(name, status, brand, sku, categoryIds, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID",
        description = "Retrieve a specific product by its unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Product found successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product ID format")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductResponse get(
            @Parameter(description = "Product unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable @NotNull UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(
        summary = "Create a new product",
        description = "Create a new product with the provided information."
    )
    @ApiResponse(responseCode = "201", description = "Product created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid product data")
    @ApiResponse(responseCode = "409", description = "Product with same SKU already exists")
    public ResponseEntity<ProductResponse> create(
            @Parameter(description = "Product creation data", required = true)
            @Validated(ValidationGroups.Create.class) @RequestBody ProductRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update existing product",
        description = "Update an existing product with new information."
    )
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product data or ID format")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "409", description = "Product with same SKU already exists")
    public ProductResponse update(
            @Parameter(description = "Product unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,

            @Parameter(description = "Updated product data", required = true)
            @Validated(ValidationGroups.Update.class) @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete product",
        description = "Delete an existing product from the system."
    )
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product ID format")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Product unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable @NotNull UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
