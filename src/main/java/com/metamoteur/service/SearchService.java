package com.metamoteur.service;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.entity.SearchResult;
import com.metamoteur.exception.SearchException;
import com.metamoteur.repository.SearchRepository;
import com.metamoteur.repository.SearchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service principal de gestion des recherches
 * Toutes les opérations sont sécurisées et transactionnelles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchService {

    private final SearchRepository searchRepository;
    private final SearchResultRepository resultRepository;

    /**
     * Sauvegarde sécurisée d'une recherche avec ses résultats
     */
    @Transactional
    public Search saveSearch(String keywords, List<SearchResultDTO> resultDTOs) {
        // Validation et sanitization
        String cleanKeywords = sanitizeKeywords(keywords);

        Search search = Search.builder()
                .keywords(cleanKeywords)
                .timestamp(Instant.now())
                .build();

        // Conversion DTOs -> Entités et association bidirectionnelle
        resultDTOs.forEach(dto -> {
            SearchResult result = SearchResult.builder()
                    .url(sanitizeUrl(dto.getUrl()))
                    .title(sanitizeText(dto.getTitle(), 500))
                    .description(sanitizeText(dto.getDescription(), 5000))
                    .rank(dto.getRank())
                    .clickCount(0)
                    .build();

            search.addResult(result);
        });

        Search saved = searchRepository.save(search);
        log.info("Saved search: id={}, keywords={}, results={}",
                saved.getId(), saved.getKeywords(), saved.getResults().size());

        return saved;
    }

    /**
     * Tracking sécurisé des clics sur URLs
     */
    @Transactional
    public void trackClick(String url) {
        String cleanUrl = sanitizeUrl(url);

        // Recherche des recherches récentes (< 30 min)
        Instant thirtyMinutesAgo = Instant.now().minus(30, ChronoUnit.MINUTES);
        List<Search> recentSearches = searchRepository.findByTimestampAfter(thirtyMinutesAgo);

        if (recentSearches.isEmpty()) {
            log.debug("No recent searches found for click tracking");
            return;
        }

        // Mise à jour via repository (sécurisé)
        int updated = resultRepository.incrementClickCountByUrl(cleanUrl);

        log.info("Click tracked for URL: {} (updated {} results)", cleanUrl, updated);
    }

    /**
     * Recherche de cas similaires pour permutations
     */
    public List<Search> findSimilarSearches(String keywords, int limit) {
        String cleanKeywords = sanitizeKeywords(keywords);

        try {
            return searchRepository.findSimilarSearches(cleanKeywords, limit);
        } catch (Exception e) {
            log.warn("Full-text search failed, falling back to LIKE query", e);
            // Fallback sur recherche simple
            return searchRepository.findByKeywordsContainingIgnoreCase(cleanKeywords)
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Nettoyage périodique des anciennes données
     */
    @Transactional
    public int cleanupOldSearches(int daysToKeep) {
        Instant cutoff = Instant.now().minus(daysToKeep, ChronoUnit.DAYS);
        int deleted = searchRepository.deleteOldSearches(cutoff);

        log.info("Cleaned up {} searches older than {} days", deleted, daysToKeep);
        return deleted;
    }

    /**
     * Sanitization des mots-clés
     */
    public String sanitizeKeywords(String keywords) {
        if (keywords == null) return "";

        // Trim, limite longueur, enlève caractères dangereux
        return keywords.trim()
                .replaceAll("[<>\"']", "")
                .substring(0, Math.min(keywords.length(), 500));
    }

    /**
     * Sanitization des URLs
     */
    public String sanitizeUrl(String url) {
        if (url == null) return "";

        // Validation basique d'URL
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new SearchException("Invalid URL format: " + url);
        }

        return url.trim().substring(0, Math.min(url.length(), 2000));
    }

    /**
     * Sanitization texte générique
     */
    public String sanitizeText(String text, int maxLength) {
        if (text == null) return "";

        return text.trim()
                .replaceAll("[<>]", "")
                .substring(0, Math.min(text.length(), maxLength));
    }

    /**
     * Conversion entité -> DTO
     */
    public SearchResultDTO toDTO(SearchResult result) {
        return SearchResultDTO.builder()
                .id(result.getId())
                .url(result.getUrl())
                .title(result.getTitle())
                .description(result.getDescription())
                .rank(result.getRank())
                .clickCount(result.getClickCount())
                .score(result.calculateScore())
                .build();
    }
}