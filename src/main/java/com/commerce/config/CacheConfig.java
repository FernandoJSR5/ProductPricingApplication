package com.commerce.config;

import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The type Cache config.
 */
@Configuration
@EnableAsync
public class CacheConfig {

    /**
     * Async cache manager caffeine cache manager.
     *
     * @return the caffeine cache manager
     */
    @Bean
    public CaffeineCacheManager asyncCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }
}
