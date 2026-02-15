package com.metamoteur.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamoteur.dto.SearchRequest;
import com.metamoteur.dto.SearchResponse;
import com.metamoteur.entity.Search;
import com.metamoteur.repository.SearchRepository;
import com.metamoteur.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SearchRepository searchRepository;

    @BeforeEach
    void setUp() {
        searchRepository.deleteAll();
    }

    @Test
    void endToEndSearch_ShouldSaveToDatabase() throws Exception {
        // Given
        SearchRequest request = TestDataBuilder.searchRequest()
                .query("java programming")
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        SearchResponse response = objectMapper.readValue(responseBody, SearchResponse.class);

        assertNotNull(response);
        assertEquals("java programming", response.getQuery());

        // Vérifier la sauvegarde en BDD
        List<Search> savedSearches = searchRepository.findAll();
        assertFalse(savedSearches.isEmpty());

        Search savedSearch = savedSearches.get(0);
        assertEquals("java programming", savedSearch.getKeywords());
    }

    @Test
    void multipleSearches_ShouldAccumulateHistory() throws Exception {
        // Given
        String[] queries = {"java", "python", "javascript"};

        // When - effectuer plusieurs recherches
        for (String query : queries) {
            SearchRequest request = TestDataBuilder.searchRequest()
                    .query(query)
                    .build();

            mockMvc.perform(post("/api/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // Then
        List<Search> allSearches = searchRepository.findAll();
        assertEquals(3, allSearches.size());
    }
}