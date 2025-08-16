package com.loiane.product.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Service for monitoring cache statistics and performance.
 * Provides detailed metrics about cache usage, hit rates, and performance.
 *
 * @author Loiane Groner
 * @since 1.0.0
 */
@Component
public class CacheMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(CacheMetricsService.class);

    private final CacheManager cacheManager;

    public CacheMetricsService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Logs cache statistics periodically for monitoring.
     * Runs every 5 minutes to track cache performance.
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void logCacheStatistics() {
        logger.info("=== Cache Statistics Report ===");

        cacheManager.getCacheNames().forEach(this::logCacheStats);

        logger.info("=== End Cache Statistics ===");
    }

    /**
     * Logs detailed statistics for a specific cache.
     *
     * @param cacheName the name of the cache to analyze
     */
    private void logCacheStats(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache caffeineCache) {
            Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
            CacheStats stats = nativeCache.stats();

            long totalRequests = stats.requestCount();
            long hits = stats.hitCount();
            long misses = stats.missCount();
            double hitRate = totalRequests > 0 ? (double) hits / totalRequests * 100 : 0.0;

            logger.info("Cache '{}': Size={}, Hits={}, Misses={}, Hit Rate={:.2f}%, " +
                       "Avg Load Time={:.2f}ms, Evictions={}",
                cacheName,
                nativeCache.estimatedSize(),
                hits,
                misses,
                hitRate,
                stats.averageLoadPenalty() / 1_000_000.0, // Convert nanoseconds to milliseconds
                stats.evictionCount()
            );
        }
    }

    /**
     * Gets cache statistics for a specific cache.
     *
     * @param cacheName the name of the cache
     * @return cache statistics or null if cache not found
     */
    public CacheStatistics getCacheStatistics(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache caffeineCache) {
            Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
            CacheStats stats = nativeCache.stats();

            return new CacheStatistics(
                cacheName,
                nativeCache.estimatedSize(),
                stats.hitCount(),
                stats.missCount(),
                stats.requestCount(),
                stats.hitRate(),
                stats.averageLoadPenalty() / 1_000_000.0, // Convert to milliseconds
                stats.evictionCount()
            );
        }
        return null;
    }

    /**
     * Gets overall cache health summary.
     *
     * @return cache health summary
     */
    public CacheHealthSummary getCacheHealthSummary() {
        long totalSize = 0;
        long totalHits = 0;
        long totalMisses = 0;
        double averageHitRate = 0.0;
        int cacheCount = 0;

        for (String cacheName : cacheManager.getCacheNames()) {
            CacheStatistics stats = getCacheStatistics(cacheName);
            if (stats != null) {
                totalSize += stats.size();
                totalHits += stats.hitCount();
                totalMisses += stats.missCount();
                averageHitRate += stats.hitRate();
                cacheCount++;
            }
        }

        if (cacheCount > 0) {
            averageHitRate = averageHitRate / cacheCount;
        }

        return new CacheHealthSummary(
            cacheCount,
            totalSize,
            totalHits,
            totalMisses,
            averageHitRate,
            isHealthy(averageHitRate)
        );
    }

    /**
     * Determines if cache performance is healthy based on hit rate.
     */
    private boolean isHealthy(double averageHitRate) {
        return averageHitRate >= 0.7; // Consider healthy if hit rate >= 70%
    }

    /**
     * Cache statistics data class.
     */
    public record CacheStatistics(
        String cacheName,
        long size,
        long hitCount,
        long missCount,
        long requestCount,
        double hitRate,
        double averageLoadTimeMs,
        long evictionCount
    ) {}

    /**
     * Cache health summary data class.
     */
    public record CacheHealthSummary(
        int cacheCount,
        long totalSize,
        long totalHits,
        long totalMisses,
        double averageHitRate,
        boolean isHealthy
    ) {}
}
