package com.metamoteur.client;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.exception.SearchEngineUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleSearchClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleSearchClient googleSearchClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleSearchClient, "apiKey", "test-key");
        ReflectionTestUtils.setField(googleSearchClient, "engineId", "test-engine");
        ReflectionTestUtils.setField(googleSearchClient, "defaultMaxResults", 20);
    }

    @Test
    void search_WithValidResponse_ShouldReturnResults() {
        // Given
        GoogleSearchClient.GoogleSearchResponse mockResponse = createMockResponse(5);
        when(restTemplate.getForEntity(any(), eq(GoogleSearchClient.GoogleSearchResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // When
        List<SearchResultDTO> results = googleSearchClient.search("test query", 10);

        // Then
        assertNotNull(results);
        assertEquals(5, results.size());
        assertEquals("Google API", results.get(0).getSource());
    }

    @Test
    void search_WithoutApiKey_ShouldThrowException() {
        // Given
        ReflectionTestUtils.setField(googleSearchClient, "apiKey", "");

        // When/Then
        assertThrows(SearchEngineUnavailableException.class, () ->
                googleSearchClient.search("test", 10)
        );
    }

    @Test
    void search_WithRateLimitError_ShouldThrowException() {
        // Given
        when(restTemplate.getForEntity(any(), eq(GoogleSearchClient.GoogleSearchResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        // When/Then
        assertThrows(SearchEngineUnavailableException.class, () ->
                googleSearchClient.search("test", 10)
        );
    }

    @Test
    void isAvailable_WithConfigured_ShouldReturnTrue() {
        // When/Then
        assertTrue(googleSearchClient.isAvailable());
    }

    @Test
    void getPriority_ShouldReturn1() {
        // When/Then
        assertEquals(1, googleSearchClient.getPriority());
    }

    private GoogleSearchClient.GoogleSearchResponse createMockResponse(int itemCount) {
        GoogleSearchClient.GoogleSearchResponse response = new GoogleSearchClient.GoogleSearchResponse();
        List<GoogleSearchClient.GoogleSearchItem> items = new java.util.ArrayList<>();

        for (int i = 1; i <= itemCount; i++) {
            GoogleSearchClient.GoogleSearchItem item = new GoogleSearchClient.GoogleSearchItem();
            item.setTitle("Result " + i);
            item.setLink("https://example.com/result" + i);
            item.setSnippet("Description " + i);
            items.add(item);
        }

        response.setItems(items);
        return response;
    }
}