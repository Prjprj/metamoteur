package com.metamoteur.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

/**
 * Configuration du cache Caffeine
 * Alternative: Redis pour environnements distribués
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * Cache manager avec Caffeine (en mémoire)
     */
    @Bean
    @Profile("!prod")
    public CacheManager caffeineCacheManager() {
        log.info("Configuring Caffeine cache manager (in-memory)");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "googleSearchResults",
                "searchHistory",
                "agentResponses",
                "urlTracking"
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats() // Pour monitoring
        );

        return cacheManager;
    }

    /**
     * Configuration Redis pour production
     * Nécessite: spring-boot-starter-data-redis
     */
    @Bean
    @Profile("prod")
    public CacheManager redisCacheManager(
            org.springframework.data.redis.connection.RedisConnectionFactory connectionFactory
    ) {
        log.info("Configuring Redis cache manager (distributed)");

        return org.springframework.data.redis.cache.RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(
                        org.springframework.data.redis.cache.RedisCacheConfiguration
                                .defaultCacheConfig()
                                .entryTtl(java.time.Duration.ofHours(1))
                                .disableCachingNullValues()
                )
                .build();
    }
}