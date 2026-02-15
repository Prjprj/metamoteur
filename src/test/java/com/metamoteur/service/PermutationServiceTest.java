package com.metamoteur.service;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.entity.SearchResult;
import com.metamoteur.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermutationServiceTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private PermutationService permutationService;

    private List<SearchResultDTO> currentResults;

    @BeforeEach
    void setUp() {
        currentResults = TestDataBuilder.sampleResults(5);
    }

    @Test
    void permuteResults_WithNoHistory_ShouldReturnOriginalOrder() {
        // Given
        when(searchService.findSimilarSearches(anyString(), anyInt()))
                .thenReturn(List.of());

        // When
        List<SearchResultDTO> permuted = permutationService.permuteResults(
                "new query",
                currentResults
        );

        // Then
        assertEquals(currentResults.size(), permuted.size());
        // Devrait retourner l'ordre original ou similaire
    }

    @Test
    void permuteResults_WithHistory_ShouldPromoteClickedResults() {
        // Given
        Search historicalSearch = TestDataBuilder.search()
                .keywords("similar query")
                .build();

        // URL qui existe dans les résultats actuels
        SearchResult clickedResult = TestDataBuilder.searchResult()
                .url(currentResults.get(4).getUrl()) // Dernier résultat
                .rank(5)
                .clickCount(10) // Beaucoup de clics
                .build();

        historicalSearch.addResult(clickedResult);

        when(searchService.findSimilarSearches(anyString(), anyInt()))
                .thenReturn(List.of(historicalSearch));

        // When
        List<SearchResultDTO> permuted = permutationService.permuteResults(
                "test query",
                new ArrayList<>(currentResults)
        );

        // Then
        assertNotNull(permuted);
        assertEquals(currentResults.size(), permuted.size());

        // Le résultat cliqué devrait être promu (score plus élevé)
        SearchResultDTO promotedResult = permuted.stream()
                .filter(r -> r.getUrl().equals(clickedResult.getUrl()))
                .findFirst()
                .orElse(null);

        assertNotNull(promotedResult);
        assertTrue(promotedResult.getScore() > 0);
    }

    @Test
    void calculateSimilarity_WithIdenticalKeywords_ShouldReturn1() {
        // When
        double similarity = permutationService.calculateSimilarity(
                "java programming",
                "java programming"
        );

        // Then
        assertEquals(1.0, similarity, 0.01);
    }

    @Test
    void calculateSimilarity_WithCompletelyDifferent_ShouldReturn0() {
        // When
        double similarity = permutationService.calculateSimilarity(
                "java programming",
                "cooking recipes"
        );

        // Then
        assertEquals(0.0, similarity, 0.01);
    }

    @Test
    void calculateSimilarity_WithPartialMatch_ShouldReturnPartial() {
        // When
        double similarity = permutationService.calculateSimilarity(
                "java programming tutorial",
                "java tutorial"
        );

        // Then
        assertTrue(similarity > 0.0 && similarity < 1.0);
    }
}