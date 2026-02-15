package com.metamoteur.controller;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.service.PermutationService;
import com.metamoteur.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour communication inter-agents
 * Compatible avec le protocole XML original
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Slf4j
public class AgentController {

    private final SearchService searchService;
    private final PermutationService permutationService;

    /**
     * Endpoint de recherche agent-à-agent
     * POST /api/agent/search
     * Content-Type: application/xml ou application/json
     */
    @PostMapping(
            value = "/search",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<AgentSearchResponse> agentSearch(
            @RequestBody AgentSearchRequest request
    ) {
        log.info("Agent search request received: keywords={}", request.getKeywords());

        // Recherche des cas similaires locaux
        List<Search> similarSearches = searchService.findSimilarSearches(
                request.getKeywords(),
                5
        );

        if (similarSearches.isEmpty()) {
            log.debug("No similar searches found for agent request");
            return ResponseEntity.ok(AgentSearchResponse.builder()
                    .keywords(request.getKeywords())
                    .results(List.of())
                    .build());
        }

        // Permutation basée sur l'historique local
        List<SearchResultDTO> permutedResults = permutationService.permuteResults(
                request.getKeywords(),
                request.getResults()
        );

        AgentSearchResponse response = AgentSearchResponse.builder()
                .keywords(request.getKeywords())
                .results(permutedResults)
                .build();

        log.info("Agent search response sent: keywords={}, results={}",
                request.getKeywords(), permutedResults.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Ping endpoint pour vérifier disponibilité d'un agent
     * GET /api/agent/ping
     */
    @GetMapping("/ping")
    public ResponseEntity<PingResponse> ping() {
        return ResponseEntity.ok(PingResponse.builder()
                .status("UP")
                .version("2.0.0")
                .build());
    }

    /**
     * DTO pour requête agent
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AgentSearchRequest {
        private String keywords;
        private List<SearchResultDTO> results;
    }

    /**
     * DTO pour réponse agent
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AgentSearchResponse {
        private String keywords;
        private List<SearchResultDTO> results;
    }

    /**
     * DTO pour ping
     */
    @lombok.Data
    @lombok.Builder
    public static class PingResponse {
        private String status;
        private String version;
    }
}