package com.loiane.product.cache;

import com.loiane.product.cache.CacheMetricsService.CacheStatistics;
import com.loiane.product.cache.CacheMetricsService.CacheHealthSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST endpoints for cache management and monitoring.
 * Provides cache statistics, health information, and manual cache operations.
 *
 * @author Loiane Groner
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/cache")
@Tag(name = "Cache Management", description = "Cache monitoring and management operations")
public class CacheManagementController {

    private final CacheMetricsService cacheMetricsService;
    private final CacheWarmingService cacheWarmingService;

    public CacheManagementController(CacheMetricsService cacheMetricsService,
                                   CacheWarmingService cacheWarmingService) {
        this.cacheMetricsService = cacheMetricsService;
        this.cacheWarmingService = cacheWarmingService;
    }

    /**
     * Gets statistics for all caches.
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get cache statistics",
               description = "Retrieve detailed statistics for all configured caches")
    public ResponseEntity<Map<String, CacheStatistics>> getCacheStatistics() {
        var statistics = List.of("products", "productById", "categories", "categoryById")
            .stream()
            .collect(Collectors.toMap(
                cacheName -> cacheName,
                cacheMetricsService::getCacheStatistics
            ));

        return ResponseEntity.ok(statistics);
    }

    /**
     * Gets statistics for a specific cache.
     */
    @GetMapping("/statistics/{cacheName}")
    @Operation(summary = "Get specific cache statistics",
               description = "Retrieve statistics for a specific cache by name")
    public ResponseEntity<CacheStatistics> getCacheStatistics(@PathVariable String cacheName) {
        var statistics = cacheMetricsService.getCacheStatistics(cacheName);

        if (statistics == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(statistics);
    }

    /**
     * Gets overall cache health summary.
     */
    @GetMapping("/health")
    @Operation(summary = "Get cache health summary",
               description = "Retrieve overall cache performance and health metrics")
    public ResponseEntity<CacheHealthSummary> getCacheHealth() {
        var healthSummary = cacheMetricsService.getCacheHealthSummary();
        return ResponseEntity.ok(healthSummary);
    }

    /**
     * Manually triggers cache warming.
     */
    @PostMapping("/warm")
    @Operation(summary = "Warm cache",
               description = "Manually trigger cache warming for frequently accessed data")
    public ResponseEntity<String> warmCache() {
        cacheWarmingService.manualCacheWarm();
        return ResponseEntity.ok("Cache warming initiated successfully");
    }
}

/**
 * Custom Actuator endpoint for cache metrics.
 * Provides cache information through Spring Boot Actuator.
 */
@Component
@Endpoint(id = "cache-metrics")
class CacheMetricsEndpoint {

    private final CacheMetricsService cacheMetricsService;

    public CacheMetricsEndpoint(CacheMetricsService cacheMetricsService) {
        this.cacheMetricsService = cacheMetricsService;
    }

    @ReadOperation
    public Map<String, Object> cacheMetrics() {
        var healthSummary = cacheMetricsService.getCacheHealthSummary();
        var statistics = List.of("products", "productById", "categories", "categoryById")
            .stream()
            .collect(Collectors.toMap(
                cacheName -> cacheName,
                cacheMetricsService::getCacheStatistics
            ));

        return Map.of(
            "health", healthSummary,
            "statistics", statistics
        );
    }
}
