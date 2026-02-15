package com.metamoteur.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchStatsDTO {
    private Long totalSearches;
    private Long totalClicks;
    private Double averageResultsPerSearch;
    private Long uniqueUrls;
}