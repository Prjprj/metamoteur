package com.metamoteur.config;

import io.github.bucket4j.Bucket;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.mockito.Mockito.mock;

/**
 * Configuration pour les tests
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate testRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Map<String, Bucket> testRateLimitBuckets() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Function<String, Bucket> testBucketFactory() {
        return ip -> mock(Bucket.class);
    }
}