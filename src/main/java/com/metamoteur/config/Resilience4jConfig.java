package com.metamoteur.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration Resilience4j pour circuit breaker et retry
 */
@Configuration
@Slf4j
public class Resilience4jConfig {

    /**
     * Circuit Breaker pour les appels externes
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 50% d'échec
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public CircuitBreaker googleApiCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker("googleApi");

        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                        log.warn("Google API Circuit Breaker state changed: {}", event.getStateTransition())
                );

        return circuitBreaker;
    }

    /**
     * Retry pour les appels externes
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(
                        org.springframework.web.client.ResourceAccessException.class,
                        java.net.SocketTimeoutException.class
                )
                .build();

        return RetryRegistry.of(config);
    }

    @Bean
    public Retry searchRetry(RetryRegistry registry) {
        Retry retry = registry.retry("searchRetry");

        retry.getEventPublisher()
                .onRetry(event ->
                        log.debug("Retrying search attempt {}", event.getNumberOfRetryAttempts())
                );

        return retry;
    }
}