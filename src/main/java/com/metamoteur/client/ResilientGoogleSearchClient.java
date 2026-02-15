package com.metamoteur.client;

import com.metamoteur.dto.SearchResultDTO;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * GoogleSearchClient avec circuit breaker et retry
 */
@Component
@Slf4j
public class ResilientGoogleSearchClient {

    private final GoogleSearchClient delegate;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    @Autowired
    public ResilientGoogleSearchClient(
            GoogleSearchClient delegate,
            CircuitBreaker googleApiCircuitBreaker,
            Retry searchRetry
    ) {
        this.delegate = delegate;
        this.circuitBreaker = googleApiCircuitBreaker;
        this.retry = searchRetry;
    }

    public List<SearchResultDTO> search(String query, int maxResults) {
        Supplier<List<SearchResultDTO>> supplier = () ->
                delegate.search(query, maxResults);

        // Applique circuit breaker puis retry
        supplier = CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
        supplier = Retry.decorateSupplier(retry, supplier);

        return supplier.get();
    }
}