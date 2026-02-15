package com.metamoteur.service;

import com.metamoteur.client.SearchProvider;
import com.metamoteur.client.SearchProviderResponse;
import com.metamoteur.dto.SearchRequest;
import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.exception.SearchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orchestrateur de recherche multi-sources
 * Essaie plusieurs providers dans l'ordre de priorité
 */
@Service
@Slf4j
public class SearchOrchestrator {

    private final List<SearchProvider> providers;
    private final PermutationService permutationService;
    private final SearchService searchService;

    @Autowired
    public SearchOrchestrator(
            List<SearchProvider> providers,
            PermutationService permutationService,
            SearchService searchService
    ) {
        // Tri par priorité (1 = highest)
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt(SearchProvider::getPriority))
                .collect(Collectors.toList());

        this.permutationService = permutationService;
        this.searchService = searchService;

        log.info("SearchOrchestrator initialized with {} providers: {}",
                this.providers.size(),
                this.providers.stream()
                        .map(SearchProvider::getProviderName)
                        .collect(Collectors.joining(", "))
        );
    }

    /**
     * Recherche avec orchestration multi-sources
     */
    public List<SearchResultDTO> search(SearchRequest request) {
        log.info("Orchestrating search for: {}", request.getQuery());

        List<SearchResultDTO> results = null;
        SearchProviderResponse lastResponse = null;

        // Essayer les providers dans l'ordre de priorité
        for (SearchProvider provider : providers) {
            if (!provider.isAvailable()) {
                log.debug("Provider {} not available, skipping", provider.getProviderName());
                continue;
            }

            lastResponse = executeSearch(provider, request);

            if (lastResponse.isSuccess() && !lastResponse.getResults().isEmpty()) {
                results = lastResponse.getResults();
                log.info("Provider {} succeeded with {} results",
                        provider.getProviderName(), results.size());
                break;
            } else {
                log.warn("Provider {} failed or returned no results: {}",
                        provider.getProviderName(), lastResponse.getErrorMessage());
            }
        }

        // Si tous les providers ont échoué
        if (results == null || results.isEmpty()) {
            String errorMsg = lastResponse != null ? lastResponse.getErrorMessage() : "Unknown error";
            throw SearchException.allEnginesFailed();
        }

        // Application de la permutation locale
        if (request.isApplyLocalPermutation()) {
            results = permutationService.permuteResults(request.getQuery(), results);
            log.debug("Local permutation applied");
        }

        // Sauvegarde de la recherche en BDD
        try {
            searchService.saveSearch(request.getQuery(), results);
        } catch (Exception e) {
            log.error("Failed to save search in database", e);
            // Ne pas faire échouer la recherche si la sauvegarde échoue
        }

        return results;
    }

    /**
     * Exécute une recherche avec un provider
     */
    private SearchProviderResponse executeSearch(
            SearchProvider provider,
            SearchRequest request
    ) {
        long startTime = System.currentTimeMillis();

        try {
            List<SearchResultDTO> results = provider.search(
                    request.getQuery(),
                    request.getMaxResults()
            );

            long duration = System.currentTimeMillis() - startTime;

            return SearchProviderResponse.builder()
                    .providerName(provider.getProviderName())
                    .results(results)
                    .success(true)
                    .responseTimeMs(duration)
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            log.error("Provider {} failed", provider.getProviderName(), e);

            return SearchProviderResponse.builder()
                    .providerName(provider.getProviderName())
                    .results(List.of())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .responseTimeMs(duration)
                    .build();
        }
    }

    /**
     * Liste des providers disponibles
     */
    public List<String> getAvailableProviders() {
        return providers.stream()
                .filter(SearchProvider::isAvailable)
                .map(SearchProvider::getProviderName)
                .collect(Collectors.toList());
    }
}