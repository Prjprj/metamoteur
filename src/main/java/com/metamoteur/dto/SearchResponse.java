package com.metamoteur.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO pour les réponses de recherche
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private String query;

    private List<SearchResultDTO> results;

    private Integer count;

    private Instant timestamp;

    private Boolean fromCache;

    private String engine;

    private MetricsDTO metrics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricsDTO {
        private Long searchTimeMs;
        private Integer agentsContacted;
        private Integer agentsResponded;
        private Boolean localPermutationApplied;
    }
}