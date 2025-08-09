package com.loiane.product.product.api;

import com.loiane.product.product.ProductService;
import com.loiane.product.product.api.dto.ProductRequest;
import com.loiane.product.product.api.dto.ProductResponse;
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
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management operations")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all products", description = "Retrieve a paginated list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    public Page<ProductResponse> list(
            @Parameter(description = "Pagination information")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.listAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by various criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    })
    public Page<ProductResponse> search(
            @Parameter(description = "Product name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Product status (ACTIVE, INACTIVE, DRAFT)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Product brand (partial match)")
            @RequestParam(required = false) String brand,
            @Parameter(description = "Product SKU (partial match)")
            @RequestParam(required = false) String sku,
            @Parameter(description = "Set of category IDs to filter by")
            @RequestParam(required = false) Set<UUID> categoryIds,
            @Parameter(description = "Pagination information")
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(name, status, brand, sku, categoryIds, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponse get(
            @Parameter(description = "Product unique identifier")
            @PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    public ResponseEntity<ProductResponse> create(
            @Parameter(description = "Product creation data")
            @Valid @RequestBody ProductRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Update an existing product with new information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponse update(
            @Parameter(description = "Product unique identifier")
            @PathVariable UUID id,
            @Parameter(description = "Product update data")
            @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Product unique identifier")
            @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
