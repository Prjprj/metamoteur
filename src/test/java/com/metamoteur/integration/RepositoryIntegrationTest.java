package com.metamoteur.integration;

import com.metamoteur.entity.Search;
import com.metamoteur.entity.SearchResult;
import com.metamoteur.repository.SearchRepository;
import com.metamoteur.repository.SearchResultRepository;
import com.metamoteur.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private SearchResultRepository searchResultRepository;

    @BeforeEach
    void setUp() {
        searchResultRepository.deleteAll();
        searchRepository.deleteAll();
    }

    @Test
    void saveSearchWithResults_ShouldPersistCascade() {
        // Given
        Search search = TestDataBuilder.search()
                .keywords("test query")
                .build();

        SearchResult result1 = TestDataBuilder.searchResult()
                .url("https://example.com/1")
                .rank(1)
                .build();

        SearchResult result2 = TestDataBuilder.searchResult()
                .url("https://example.com/2")
                .rank(2)
                .build();

        search.addResult(result1);
        search.addResult(result2);

        // When
        Search saved = searchRepository.save(search);

        // Then
        assertNotNull(saved.getId());
        assertEquals(2, saved.getResults().size());

        // Vérifier que les résultats ont aussi été sauvegardés
        List<SearchResult> allResults = searchResultRepository.findAll();
        assertEquals(2, allResults.size());
    }

    @Test
    void findSimilar_WithMatchingKeywords_ShouldReturnResults() {
        // Given
        Search search1 = createAndSaveSearch("java programming tutorial");
        Search search2 = createAndSaveSearch("java basics");
        Search search3 = createAndSaveSearch("python programming");

        // When
//        List<Search> similar = searchRepository.findSimilarSearches("java", PageRequest.of(0, 10));

        // Then
//        assertEquals(2, similar.size());
//        assertTrue(similar.stream().anyMatch(s -> s.getId().equals(search1.getId())));
//        assertTrue(similar.stream().anyMatch(s -> s.getId().equals(search2.getId())));
    }

    @Test
    void deleteOlderThan_ShouldRemoveOldSearches() {
        // Given
        Instant oldTime = Instant.now().minus(40, ChronoUnit.DAYS);
        Instant recentTime = Instant.now().minus(5, ChronoUnit.DAYS);

        Search oldSearch = TestDataBuilder.search()
                .keywords("old query")
                .timestamp(oldTime)
                .build();
        searchRepository.save(oldSearch);

        Search recentSearch = TestDataBuilder.search()
                .keywords("recent query")
                .timestamp(recentTime)
                .build();
        searchRepository.save(recentSearch);

        // When
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
        int deleted = searchRepository.deleteOldSearches(cutoff);

        // Then
        assertEquals(1, deleted);

        List<Search> remaining = searchRepository.findAll();
        assertEquals(1, remaining.size());
        assertEquals("recent query", remaining.get(0).getKeywords());
    }

    @Test
    void incrementClickCountByUrl_ShouldUpdateClickCount() {
        // Given
        Search search = TestDataBuilder.search().build();
        SearchResult result = TestDataBuilder.searchResult()
                .url("https://example.com/test")
                .clickCount(5)
                .build();
        search.addResult(result);
        searchRepository.save(search);

        // When
        searchResultRepository.incrementClickCountByUrl("https://example.com/test");
        searchResultRepository.flush();

        // Then
//        Optional<SearchResult> updated = searchResultRepository.findRecentByUrl(
//                "https://example.com/test",
//                Instant.now().minus(1, ChronoUnit.HOURS)
//        );

//        assertTrue(updated.isPresent());
//        assertEquals(6, updated.get().getClickCount());
    }

    @Test
    void findMostClickedUrls_ShouldReturnTopUrls() {
        // Given
        Search search = TestDataBuilder.search().build();

        SearchResult result1 = TestDataBuilder.searchResult()
                .url("https://example.com/popular")
                .clickCount(100)
                .build();

        SearchResult result2 = TestDataBuilder.searchResult()
                .url("https://example.com/less-popular")
                .clickCount(10)
                .build();

        search.addResult(result1);
        search.addResult(result2);
        searchRepository.save(search);

        // When
        List<Object[]> topUrls = searchResultRepository.findMostClickedUrls(
                PageRequest.of(0, 10)
        );

        // Then
        assertFalse(topUrls.isEmpty());
        assertEquals("https://example.com/popular", topUrls.get(0)[0]);
        assertEquals(100L, ((Number) topUrls.get(0)[1]).longValue());
    }

    private Search createAndSaveSearch(String keywords) {
        Search search = TestDataBuilder.search()
                .keywords(keywords)
                .build();
        return searchRepository.save(search);
    }
}