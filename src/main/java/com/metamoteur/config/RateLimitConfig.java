package com.metamoteur.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Configuration du rate limiting avec Bucket4j
 */
@Configuration
public class RateLimitConfig {

    @Value("${metamoteur.security.rate-limit.requests-per-minute:20}")
    private int requestsPerMinute;

    /**
     * Cache des buckets par IP
     */
    @Bean
    public Map<String, Bucket> rateLimitBuckets() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Factory pour créer des buckets
     */
    @Bean
    public Function<String, Bucket> bucketFactory() {
        return ip -> createNewBucket();
    }

    /**
     * Création d'un nouveau bucket avec la limite configurée
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
                requestsPerMinute,
                Refill.intervally(requestsPerMinute, Duration.ofMinutes(1))
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}