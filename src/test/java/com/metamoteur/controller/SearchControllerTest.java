package com.metamoteur.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamoteur.dto.SearchRequest;
import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.service.SearchOrchestrator;
import com.metamoteur.service.SearchService;
import com.metamoteur.util.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SearchOrchestrator orchestrator;

    @MockBean
    private SearchService searchService;

    @Test
    void search_WithValidRequest_ShouldReturn200() throws Exception {
        // Given
        SearchRequest request = TestDataBuilder.searchRequest().build();
        List<SearchResultDTO> results = TestDataBuilder.sampleResults(5);

        when(orchestrator.search(any())).thenReturn(results);

        // When/Then
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value(request.getQuery()))
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(5));
    }

    @Test
    void search_WithEmptyQuery_ShouldReturn400() throws Exception {
        // Given
        SearchRequest request = TestDataBuilder.searchRequest()
                .query("")
                .build();

        // When/Then
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void search_WithQueryTooLong_ShouldReturn400() throws Exception {
        // Given
        String longQuery = "a".repeat(501);
        SearchRequest request = TestDataBuilder.searchRequest()
                .query(longQuery)
                .build();

        // When/Then
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_WithInvalidCharacters_ShouldReturn400() throws Exception {
        // Given
        SearchRequest request = TestDataBuilder.searchRequest()
                .query("test<script>alert('xss')</script>")
                .build();

        // When/Then
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchPaginated_WithValidParams_ShouldReturn200() throws Exception {
        // Given
        List<SearchResultDTO> results = TestDataBuilder.sampleResults(20);
        when(orchestrator.search(any())).thenReturn(results);

        // When/Then
        mockMvc.perform(get("/api/search/paginated")
                        .param("q", "java")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value("java"))
                .andExpect(jsonPath("$.results.length()").value(10))
                .andExpect(jsonPath("$.totalResults").value(20))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void redirect_WithValidUrl_ShouldReturn302() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/search/redirect")
                        .param("url", "https://example.com"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void redirect_WithLocalhost_ShouldReturn400() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/search/redirect")
                        .param("url", "http://localhost:8080/admin"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void healthCheck_ShouldReturn200() throws Exception {
        // Given
        List<SearchResultDTO> results = TestDataBuilder.sampleResults(1);
        when(orchestrator.search(any())).thenReturn(results);

        // When/Then
        mockMvc.perform(get("/api/search/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}