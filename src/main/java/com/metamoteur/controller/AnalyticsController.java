package com.metamoteur.controller;

import com.metamoteur.repository.SearchRepository;
import com.metamoteur.repository.SearchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller pour analytics et statistiques
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final SearchRepository searchRepository;
    private final SearchResultRepository resultRepository;

    /**
     * Statistiques temporelles
     * GET /api/analytics/timeline?start=2024-01-01T00:00:00Z&end=2024-01-31T23:59:59Z
     */
    @GetMapping("/timeline")
    public ResponseEntity<TimelineStatsDTO> getTimeline(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        log.info("Timeline stats requested: start={}, end={}", start, end);

        // Validation
        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().build();
        }

        List<Object[]> stats = searchRepository.getSearchStatistics(start, end);

        Map<String, Long> dailyStats = stats.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).longValue()
                ));

        TimelineStatsDTO response = TimelineStatsDTO.builder()
                .startDate(start)
                .endDate(end)
                .dailySearches(dailyStats)
                .totalSearches(dailyStats.values().stream().mapToLong(Long::longValue).sum())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * URLs les plus populaires
     * GET /api/analytics/top-urls?limit=10
     */
    @GetMapping("/top-urls")
    public ResponseEntity<List<TopUrlDTO>> getTopUrls(
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("Top URLs requested: limit={}", limit);

        List<Object[]> topUrls = resultRepository.findMostClickedUrls(
                PageRequest.of(0, limit)
        );

        List<TopUrlDTO> response = topUrls.stream()
                .map(row -> TopUrlDTO.builder()
                        .url((String) row[0])
                        .clicks(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Recherches populaires
     * GET /api/analytics/trending?days=7
     */
    @GetMapping("/trending")
    public ResponseEntity<List<TrendingSearchDTO>> getTrending(
            @RequestParam(defaultValue = "7") int days
    ) {
        log.info("Trending searches requested: days={}", days);

        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);

        var trending = searchRepository.findMostPopular(
                since,
                PageRequest.of(0, 10)
        );

        List<TrendingSearchDTO> response = trending.getContent().stream()
                .map(search -> TrendingSearchDTO.builder()
                        .keywords(search.getKeywords())
                        .searchCount(1L) // TODO: Compter réellement
                        .totalClicks(search.getResults().stream()
                                .mapToLong(r -> r.getClickCount())
                                .sum())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * DTOs pour analytics
     */
    @lombok.Data
    @lombok.Builder
    public static class TimelineStatsDTO {
        private Instant startDate;
        private Instant endDate;
        private Map<String, Long> dailySearches;
        private Long totalSearches;
    }

    @lombok.Data
    @lombok.Builder
    public static class TopUrlDTO {
        private String url;
        private Long clicks;
    }

    @lombok.Data
    @lombok.Builder
    public static class TrendingSearchDTO {
        private String keywords;
        private Long searchCount;
        private Long totalClicks;
    }
}