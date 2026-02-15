package com.metamoteur.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class AnalyticsDTO {
    private Instant periodStart;
    private Instant periodEnd;
    private Map<String, Long> searchesByDay;
    private Map<String, Long> clicksByUrl;
    private Long totalSearches;
    private Long totalClicks;
}