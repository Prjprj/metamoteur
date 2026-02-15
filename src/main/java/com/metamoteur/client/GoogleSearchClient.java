package com.metamoteur.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.exception.SearchEngineUnavailableException;
import com.metamoteur.exception.SearchException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Client pour Google Custom Search API
 * https://developers.google.com/custom-search/v1/overview
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleSearchClient implements SearchProvider {

    private static final String GOOGLE_API_URL = "https://www.googleapis.com/customsearch/v1";

    private final RestTemplate restTemplate;

    @Value("${google.search.api-key:}")
    private String apiKey;

    @Value("${google.search.engine-id:}")
    private String engineId;

    @Value("${google.search.max-results:20}")
    private int defaultMaxResults;

    /**
     * Recherche avec cache
     */
    @Override
    @Cacheable(value = "googleSearchResults", key = "#query + '_' + #maxResults", unless = "#result == null || #result.isEmpty()")
    public List<SearchResultDTO> search(String query, int maxResults) {
        log.info("Google API search: query='{}', maxResults={}", query, maxResults);

        if (!isConfigured()) {
            throw new SearchEngineUnavailableException("Google API not configured");
        }

        try {
            List<SearchResultDTO> allResults = new ArrayList<>();
            int remaining = Math.min(maxResults, defaultMaxResults);
            int startIndex = 1;

            // Google API limite à 10 résultats par requête
            while (remaining > 0 && startIndex <= 91) { // Max 100 résultats total
                int numResults = Math.min(10, remaining);

                URI uri = buildSearchUri(query, startIndex, numResults);
                ResponseEntity<GoogleSearchResponse> response =
                        restTemplate.getForEntity(uri, GoogleSearchResponse.class);

                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    throw SearchException.engineFailure("Google",
                            new RuntimeException("Invalid response: " + response.getStatusCode()));
                }

                List<SearchResultDTO> pageResults = convertToSearchResults(
                        response.getBody(),
                        startIndex
                );

                allResults.addAll(pageResults);

                // Si moins de résultats que demandé, on a atteint la fin
                if (pageResults.size() < numResults) {
                    break;
                }

                remaining -= pageResults.size();
                startIndex += 10;
            }

            log.info("Google API returned {} results for query: {}", allResults.size(), query);
            return allResults;

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Google API rate limit exceeded");
            throw new SearchEngineUnavailableException("Google", e);

        } catch (HttpClientErrorException e) {
            log.error("Google API error: {}", e.getMessage());
            throw SearchException.engineFailure("Google", e);

        } catch (Exception e) {
            log.error("Unexpected error calling Google API", e);
            throw SearchException.engineFailure("Google", e);
        }
    }

    @Override
    public String getProviderName() {
        return "Google Custom Search API";
    }

    @Override
    public int getPriority() {
        return 1; // Highest priority
    }

    @Override
    public boolean isAvailable() {
        return isConfigured();
    }

    /**
     * Vérifie si l'API est configurée
     */
    private boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty()
                && engineId != null && !engineId.isEmpty();
    }

    /**
     * Construction de l'URI de recherche
     */
    private URI buildSearchUri(String query, int startIndex, int numResults) {
        return UriComponentsBuilder.fromHttpUrl(GOOGLE_API_URL)
                .queryParam("key", apiKey)
                .queryParam("cx", engineId)
                .queryParam("q", query)
                .queryParam("start", startIndex)
                .queryParam("num", numResults)
                .queryParam("lr", "lang_fr") // Langue française prioritaire
                .queryParam("safe", "active") // Safe search
                .build()
                .toUri();
    }

    /**
     * Conversion de la réponse Google en SearchResultDTO
     */
    private List<SearchResultDTO> convertToSearchResults(
            GoogleSearchResponse response,
            int startRank
    ) {
        if (response.getItems() == null || response.getItems().isEmpty()) {
            return List.of();
        }

        int rank = startRank;
        List<SearchResultDTO> results = new ArrayList<>();

        for (GoogleSearchItem item : response.getItems()) {
            SearchResultDTO result = SearchResultDTO.builder()
                    .url(item.getLink())
                    .title(item.getTitle())
                    .description(item.getSnippet())
                    .rank(rank++)
                    .clickCount(0)
                    .score(0.0)
                    .source("Google API")
                    .build();

            results.add(result);
        }

        return results;
    }

    /**
     * DTOs pour la réponse Google
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoogleSearchResponse {
        private List<GoogleSearchItem> items;

        @JsonProperty("searchInformation")
        private SearchInformation searchInformation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoogleSearchItem {
        private String title;
        private String link;
        private String snippet;
        private String displayLink;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchInformation {
        private String totalResults;
        private double searchTime;
    }
}