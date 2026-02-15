package com.metamoteur.performance;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.repository.SearchRepository;
import com.metamoteur.service.PermutationService;
import com.metamoteur.service.SearchService;
import com.metamoteur.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests de performance basiques
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PerformanceTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private PermutationService permutationService;

    @Autowired
    private SearchRepository searchRepository;

    @BeforeEach
    void setUp() {
        searchRepository.deleteAll();
    }

    @Test
    void permutation_With100Results_ShouldCompleteQuickly() {
        // Given
        List<SearchResultDTO> results = TestDataBuilder.sampleResults(100);

        // Créer de l'historique
        for (int i = 0; i < 10; i++) {
            Search search = TestDataBuilder.search()
                    .keywords("test query " + i)
                    .build();
            searchRepository.save(search);
        }

        // When
        Instant start = Instant.now();
        List<SearchResultDTO> permuted = permutationService.permuteResults(
                "test query",
                results
        );
        Duration duration = Duration.between(start, Instant.now());

        // Then
        assertTrue(duration.toMillis() < 1000,
                "Permutation should complete in less than 1 second, took " + duration.toMillis() + "ms");
        assertEquals(100, permuted.size());
    }

    @Test
    void bulkInsert_1000Searches_ShouldCompleteQuickly() {
        // Given
        List<Search> searches = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Search search = TestDataBuilder.search()
                    .keywords("bulk test " + i)
                    .build();
            searches.add(search);
        }

        // When
        Instant start = Instant.now();
        searchRepository.saveAll(searches);
        searchRepository.flush();
        Duration duration = Duration.between(start, Instant.now());

        // Then
        assertTrue(duration.toMillis() < 5000,
                "Bulk insert should complete in less than 5 seconds, took " + duration.toMillis() + "ms");
    }

    @Test
    void similaritySearch_WithLargeDataset_ShouldBeFast() {
        // Given - Créer 1000 recherches
        for (int i = 0; i < 1000; i++) {
            Search search = TestDataBuilder.search()
                    .keywords("query " + (i % 100))
                    .build();
            searchRepository.save(search);
        }
        searchRepository.flush();

        // When
        Instant start = Instant.now();
        List<Search> similar = searchService.findSimilarSearches("query 50", 10);
        Duration duration = Duration.between(start, Instant.now());

        // Then
        assertTrue(duration.toMillis() < 500,
                "Similarity search should complete in less than 500ms, took " + duration.toMillis() + "ms");
        assertTrue(similar.size() > 0);
    }
}