package com.loiane.product.cache;

import com.loiane.product.category.CategoryService;
import com.loiane.product.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Service responsible for cache warming strategies.
 * Preloads frequently accessed data into cache on application startup
 * and periodically refreshes popular items.
 *
 * @author Loiane Groner
 * @since 1.0.0
 */
@Component
public class CacheWarmingService {

    private static final Logger logger = LoggerFactory.getLogger(CacheWarmingService.class);

    private final ProductService productService;
    private final CategoryService categoryService;

    public CacheWarmingService(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Warms up the cache on application startup.
     * Runs asynchronously to avoid blocking application startup.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void warmCacheOnStartup() {
        logger.info("Starting cache warming process...");

        try {
            // Warm up categories cache - load all categories as they're frequently accessed
            warmCategoriesCache();

            // Warm up popular products cache - load first page of products
            warmPopularProductsCache();

            logger.info("Cache warming completed successfully");
        } catch (Exception e) {
            logger.error("Error during cache warming", e);
        }
    }

    /**
     * Periodically refreshes popular items in cache.
     * Runs every 15 minutes to keep popular data fresh.
     */
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void refreshPopularItems() {
        logger.debug("Refreshing popular items in cache...");

        try {
            // Refresh categories (they don't change often but are frequently accessed)
            warmCategoriesCache();

            // Refresh popular products
            warmPopularProductsCache();

            logger.debug("Popular items cache refresh completed");
        } catch (Exception e) {
            logger.warn("Error during popular items cache refresh", e);
        }
    }

    /**
     * Warms up the categories cache by loading all categories.
     */
    private void warmCategoriesCache() {
        logger.debug("Warming categories cache...");

        // Load all categories into cache
        categoryService.listAll();

        logger.debug("Categories cache warmed successfully");
    }

    /**
     * Warms up the products cache by loading the most popular/recent products.
     */
    private void warmPopularProductsCache() {
        logger.debug("Warming popular products cache...");

        // Load first 20 products sorted by name (most likely to be accessed)
        var pageable = PageRequest.of(0, 20, Sort.by("name"));
        productService.listAll(pageable);

        logger.debug("Popular products cache warmed successfully");
    }

    /**
     * Manually triggers cache warming - useful for testing or manual refresh.
     */
    public void manualCacheWarm() {
        logger.info("Manual cache warming triggered");

        try {
            // Execute cache warming synchronously for manual triggers
            warmCategoriesCache();
            warmPopularProductsCache();
            logger.info("Manual cache warming completed successfully");
        } catch (Exception e) {
            logger.error("Error during manual cache warming", e);
        }
    }
}
