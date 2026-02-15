package com.metamoteur.service;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.entity.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de permutation des résultats basé sur l'historique
 * Implémentation de l'algorithme de vote original
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermutationService {

    private final SearchService searchService;

    @Value("${metamoteur.search.similarity-threshold:0.7}")
    private double similarityThreshold;

    /**
     * Permute les résultats en fonction de l'historique local
     */
    public List<SearchResultDTO> permuteResults(
            String keywords,
            List<SearchResultDTO> originalResults
    ) {
        log.debug("Starting permutation for keywords: {}", keywords);

        // Recherche des cas similaires dans l'historique
        List<Search> similarSearches = searchService.findSimilarSearches(keywords, 10);

        if (similarSearches.isEmpty()) {
            log.debug("No similar searches found, returning original results");
            return originalResults;
        }

        // Association des rangs historiques
        Map<String, Double> urlToAverageRank = calculateAverageRanks(similarSearches);

        // Permutation par vote
        List<SearchResultDTO> permuted = voteAndPermute(originalResults, urlToAverageRank);

        log.info("Permutation complete: {} similar searches used", similarSearches.size());
        return permuted;
    }

    /**
     * Calcule les rangs moyens des URLs dans l'historique
     */
    private Map<String, Double> calculateAverageRanks(List<Search> searches) {
        Map<String, List<Integer>> urlRanks = new HashMap<>();

        for (Search search : searches) {
            for (SearchResult result : search.getResults()) {
                urlRanks.computeIfAbsent(result.getUrl(), k -> new ArrayList<>())
                        .add(result.getRank());
            }
        }

        return urlRanks.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .mapToInt(Integer::intValue)
                                .average()
                                .orElse(999.0)
                ));
    }

    /**
     * Vote et permutation des résultats
     */
    private List<SearchResultDTO> voteAndPermute(
            List<SearchResultDTO> results,
            Map<String, Double> historicalRanks
    ) {
        List<SearchResultDTO> sorted = new ArrayList<>(results);

        // Tri par vote comparatif
        sorted.sort((r1, r2) -> {
            double vote = calculateVote(
                    r1.getRank(),
                    r2.getRank(),
                    historicalRanks.getOrDefault(r1.getUrl(), 999.0),
                    historicalRanks.getOrDefault(r2.getUrl(), 999.0)
            );

            if (vote < 0) return -1;
            if (vote > 0) return 1;
            return 0;
        });

        // Mise à jour des rangs
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setRank(i + 1);
        }

        return sorted;
    }

    /**
     * Calcul du vote (algorithme original)
     */
    private double calculateVote(
            int rank1Current,
            int rank2Current,
            double rank1Historical,
            double rank2Historical
    ) {
        return ((rank1Current - rank2Current) + (rank1Historical - rank2Historical)) / 2.0;
    }

    /**
     * Calcul de similarité Jaccard entre deux textes
     */
    public double calculateSimilarity(String text1, String text2) {
        Set<String> words1 = tokenize(text1);
        Set<String> words2 = tokenize(text2);

        if (words1.isEmpty() || words2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return (double) intersection.size() / union.size();
    }

    /**
     * Tokenisation simple d'un texte
     */
    private Set<String> tokenize(String text) {
        if (text == null) return Set.of();

        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(w -> w.length() > 2)
                .collect(Collectors.toSet());
    }
}