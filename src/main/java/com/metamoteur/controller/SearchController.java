package com.metamoteur.controller;

import com.metamoteur.dto.*;
import com.metamoteur.service.SearchOrchestrator;
import com.metamoteur.service.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST Controller principal pour les recherches
 * Sécurisé avec validation, encodage et rate limiting
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "${metamoteur.security.allowed-origins}")
public class SearchController {

    private final SearchOrchestrator orchestrator;
    private final SearchService searchService;

    /**
     * Endpoint principal de recherche
     * POST /api/search
     */
    @PostMapping
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        log.info("Search request received: query='{}', engine={}",
                request.getQuery(), request.getEngine());

        long startTime = System.currentTimeMillis();

        try {
            // Encodage anti-XSS (defense in depth)
            String safeQuery = Encode.forHtml(request.getQuery().trim());

            // Orchestration de la recherche
            List<SearchResultDTO> results = orchestrator.search(request);

            long duration = System.currentTimeMillis() - startTime;

            SearchResponse response = SearchResponse.builder()
                    .query(safeQuery)
                    .results(results)
                    .count(results.size())
                    .timestamp(Instant.now())
                    .fromCache(false) // À implémenter avec cache
                    .engine(request.getEngine())
                    .metrics(SearchResponse.MetricsDTO.builder()
                            .searchTimeMs(duration)
                            .agentsContacted(0) // À implémenter
                            .agentsResponded(0)
                            .localPermutationApplied(true)
                            .build())
                    .build();

            log.info("Search completed: query='{}', results={}, duration={}ms",
                    safeQuery, results.size(), duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Search failed for query: {}", request.getQuery(), e);
            throw e;
        }
    }

    /**
     * Endpoint de recherche paginée
     * GET /api/search/paginated?q=java&page=0&size=20
     */
    @GetMapping("/paginated")
    public ResponseEntity<PagedSearchResponse> searchPaginated(
            @RequestParam("q") @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,']+$") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Paginated search: query='{}', page={}, size={}", query, page, size);

        // Validation de la pagination
        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest().build();
        }

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .maxResults(100) // Récupérer plus pour pagination
                .build();

        List<SearchResultDTO> allResults = orchestrator.search(request);
        PagedSearchResponse response = PagedSearchResponse.from(query, allResults, page, size);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de suggestion/autocomplétion
     * GET /api/search/suggestions?q=jav
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(
            @RequestParam("q") @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_]+$") String query
    ) {
        log.debug("Suggestions request: query='{}'", query);

        if (query.length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        // Recherche dans l'historique pour suggestions
        List<String> suggestions = searchService.findSimilarSearches(query, 10)
                .stream()
                .map(search -> search.getKeywords())
                .distinct()
                .limit(5)
                .toList();

        return ResponseEntity.ok(suggestions);
    }

    /**
     * Redirection avec tracking de clics
     * GET /api/search/redirect?url=https://example.com
     */
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirect(
            @RequestParam("url") @Pattern(regexp = "^https?://.*") String url
    ) {
        log.info("Redirect request: url={}", url);

        // Validation stricte de l'URL
        if (!isValidUrl(url)) {
            log.warn("Invalid URL rejected: {}", url);
            return ResponseEntity.badRequest().build();
        }

        // Tracking asynchrone du clic
        try {
            searchService.trackClick(url);
        } catch (Exception e) {
            log.error("Failed to track click for URL: {}", url, e);
            // Ne pas bloquer la redirection si le tracking échoue
        }

        // Redirection 302 temporaire
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }

    /**
     * Statistiques de recherche
     * GET /api/search/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<SearchStatsDTO> getStats() {
        // TODO: Implémenter statistiques
        SearchStatsDTO stats = SearchStatsDTO.builder()
                .totalSearches(0L)
                .totalClicks(0L)
                .averageResultsPerSearch(0.0)
                .build();

        return ResponseEntity.ok(stats);
    }

    /**
     * Health check spécifique au moteur de recherche
     * GET /api/search/health
     */
    @GetMapping("/health")
    public ResponseEntity<HealthCheckDTO> healthCheck() {
        try {
            // Test recherche basique
            SearchRequest testRequest = SearchRequest.builder()
                    .query("test")
                    .maxResults(1)
                    .build();

            orchestrator.search(testRequest);

            return ResponseEntity.ok(HealthCheckDTO.builder()
                    .status("UP")
                    .timestamp(Instant.now())
                    .build());

        } catch (Exception e) {
            log.error("Health check failed", e);
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(HealthCheckDTO.builder()
                            .status("DOWN")
                            .timestamp(Instant.now())
                            .error(e.getMessage())
                            .build());
        }
    }

    /**
     * Validation d'URL
     */
    private boolean isValidUrl(String url) {
        try {
            java.net.URL parsedUrl = new java.net.URL(url);
            String host = parsedUrl.getHost().toLowerCase();

            // Bloquer les URLs internes/locales
            return !host.contains("localhost") &&
                    !host.contains("127.0.0.1") &&
                    !host.contains("0.0.0.0") &&
                    !host.contains("192.168.") &&
                    !host.contains("10.0.");
        } catch (Exception e) {
            return false;
        }
    }
}