package com.metamoteur.service;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.entity.SearchResult;
import com.metamoteur.exception.InvalidSearchQueryException;
import com.metamoteur.repository.SearchRepository;
import com.metamoteur.repository.SearchResultRepository;
import com.metamoteur.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchResultRepository searchResultRepository;

    @InjectMocks
    private SearchService searchService;

    private Search testSearch;
    private List<SearchResultDTO> testResults;

    @BeforeEach
    void setUp() {
        testSearch = TestDataBuilder.search()
                .id(1L)
                .keywords("java programming")
                .build();

        testResults = TestDataBuilder.sampleResults(5);
    }

    @Test
    void saveSearch_WithValidData_ShouldSaveSuccessfully() {
        // Given
        when(searchRepository.save(any(Search.class))).thenReturn(testSearch);

        // When
        Search saved = searchService.saveSearch("java programming", testResults);

        // Then
        assertNotNull(saved);
        verify(searchRepository).save(any(Search.class));

        ArgumentCaptor<Search> searchCaptor = ArgumentCaptor.forClass(Search.class);
        verify(searchRepository).save(searchCaptor.capture());

        Search capturedSearch = searchCaptor.getValue();
        assertEquals("java programming", capturedSearch.getKeywords());
        assertEquals(5, capturedSearch.getResults().size());
    }

    @Test
    void saveSearch_WithEmptyQuery_ShouldThrowException() {
        // When/Then
        assertThrows(InvalidSearchQueryException.class, () ->
                searchService.saveSearch("", testResults)
        );

        verify(searchRepository, never()).save(any());
    }

    @Test
    void saveSearch_WithNullQuery_ShouldThrowException() {
        // When/Then
        assertThrows(InvalidSearchQueryException.class, () ->
                searchService.saveSearch(null, testResults)
        );

        verify(searchRepository, never()).save(any());
    }

    @Test
    void saveSearch_WithSQLInjection_ShouldThrowException() {
        // Given
        String maliciousQuery = "test'; DROP TABLE searches; --";

        // When/Then
        assertThrows(InvalidSearchQueryException.class, () ->
                searchService.saveSearch(maliciousQuery, testResults)
        );

        verify(searchRepository, never()).save(any());
    }

    @Test
    void findSimilarSearches_WithValidQuery_ShouldReturnResults() {
        // Given
        List<Search> similarSearches = List.of(testSearch);
        when(searchRepository.findSimilarSearches(anyString(), any())).thenReturn(similarSearches);

        // When
        List<Search> results = searchService.findSimilarSearches("java", 5);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(searchRepository).findSimilarSearches(eq("java"), any());
    }

    @Test
    void trackClick_WithValidUrl_ShouldIncrementClickCount() {
        // Given
        String testUrl = "https://example.com/test";
        Instant recentTime = Instant.now().minus(5, ChronoUnit.MINUTES);

        SearchResult result = TestDataBuilder.searchResult()
                .id(1L)
                .url(testUrl)
                .clickCount(5)
                .build();

        Search search = TestDataBuilder.search()
                .timestamp(recentTime)
                .build();
        search.addResult(result);

//        when(searchResultRepository.findRecentByUrl(eq(testUrl), any()))
//                .thenReturn(Optional.of(result));

        // When
        searchService.trackClick(testUrl);

        // Then
        verify(searchResultRepository).incrementClickCountByUrl(testUrl);
    }

    @Test
    void trackClick_WithInvalidUrl_ShouldNotFail() {
        // Given
        String invalidUrl = "not-a-valid-url";

        // When/Then - should not throw
        assertDoesNotThrow(() -> searchService.trackClick(invalidUrl));
    }

    @Test
    void cleanupOldSearches_ShouldDeleteOldData() {
        // Given
        when(searchRepository.deleteOldSearches(any())).thenReturn(10);

        // When
        int deleted = searchService.cleanupOldSearches(30);

        // Then
        assertEquals(10, deleted);
        verify(searchRepository).deleteOldSearches(any());
    }

    @Test
    void sanitizeKeywords_WithDangerousInput_ShouldSanitize() {
        // Given
        String dangerous = "<script>alert('xss')</script>";

        // When
        String sanitized = searchService.sanitizeKeywords(dangerous);

        // Then
        assertFalse(sanitized.contains("<script>"));
        assertFalse(sanitized.contains("</script>"));
    }

    @Test
    void sanitizeUrl_WithLocalhost_ShouldReject() {
        // Given
        String localUrl = "http://localhost:8080/admin";

        // When
        String sanitized = searchService.sanitizeUrl(localUrl);

        // Then
        assertNull(sanitized);
    }

    @Test
    void sanitizeUrl_WithValidUrl_ShouldReturnUrl() {
        // Given
        String validUrl = "https://example.com/page";

        // When
        String sanitized = searchService.sanitizeUrl(validUrl);

        // Then
        assertEquals(validUrl, sanitized);
    }
}