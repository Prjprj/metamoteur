package com.metamoteur.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatsDTO {
    private Long totalSearches;
    private Long totalResults;
    private Long totalClicks;
    private Double cacheHitRate;
    private Long averageSearchTimeMs;
    private Long databaseSize;
    private String uptime;
}