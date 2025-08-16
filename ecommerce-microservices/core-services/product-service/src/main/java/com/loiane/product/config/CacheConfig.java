package com.loiane.product.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the Product Service.
 * 
 * @author Loiane Groner
 * @since 1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Cache configuration is handled by application.yml
    // - Cache type: caffeine
    // - Named caches: products, productById, productBySku, productBySlug, 
    //   categories, categoryById, categoryBySlug
    // - TTL: 300 seconds (5 minutes)
    // - Max size: 500 entries per cache
}
