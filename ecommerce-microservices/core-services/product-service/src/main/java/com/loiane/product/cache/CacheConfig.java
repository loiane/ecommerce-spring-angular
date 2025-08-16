package com.loiane.product.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

/**
 * Cache configuration for the Product Service.
 * Provides Caffeine-based caching with statistics enabled for monitoring.
 *
 * @author Loiane Groner
 * @since 1.0.0
 */
@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
public class CacheConfig {

    /**
     * Configures the cache manager with Caffeine implementation.
     * Enables statistics recording for monitoring cache performance.
     *
     * @return configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(Duration.ofSeconds(300)) // 5 minutes
                .recordStats() // Enable statistics for monitoring
        );

        // Pre-define cache names for better management
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "products", "productById", "categories", "categoryById"
        ));

        return cacheManager;
    }
}
