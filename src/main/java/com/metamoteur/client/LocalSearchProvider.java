package com.metamoteur.client;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provider qui cherche dans l'historique local
 * Utile pour des requêtes très fréquentes
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocalSearchProvider implements SearchProvider {

    private final SearchService searchService;

    @Override
    public List<SearchResultDTO> search(String query, int maxResults) {
        log.debug("Searching in local history: query='{}'", query);

        List<Search> similarSearches = searchService.findSimilarSearches(query, 1);

        if (similarSearches.isEmpty()) {
            log.debug("No similar searches found in local history");
            return List.of();
        }

        Search mostSimilar = similarSearches.get(0);

        // Convertir les résultats historiques
        return mostSimilar.getResults().stream()
                .limit(maxResults)
                .map(result -> SearchResultDTO.builder()
                        .id(result.getId())
                        .url(result.getUrl())
                        .title(result.getTitle())
                        .description(result.getDescription())
                        .rank(result.getRank())
                        .clickCount(result.getClickCount())
                        .score(result.calculateScore())
                        .source("Local History")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String getProviderName() {
        return "Local History";
    }

    @Override
    public int getPriority() {
        return 2; // Entre Google API et JSoup
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}