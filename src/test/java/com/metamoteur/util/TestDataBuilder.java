package com.metamoteur.util;

import com.metamoteur.dto.SearchRequest;
import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.entity.Search;
import com.metamoteur.entity.SearchResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder pour données de test
 */
public class TestDataBuilder {

    public static SearchRequest.SearchRequestBuilder searchRequest() {
        return SearchRequest.builder()
                .query("test query")
                .maxResults(10)
                .engine("google")
                .useCache(true)
                .contactAgents(false);
    }

    public static SearchResultDTO.SearchResultDTOBuilder searchResultDTO() {
        return SearchResultDTO.builder()
                .url("https://example.com/test")
                .title("Test Result")
                .description("This is a test result")
                .rank(1)
                .clickCount(0)
                .score(0.0)
                .source("Test");
    }

    public static Search.SearchBuilder search() {
        return Search.builder()
                .keywords("test query")
                .timestamp(Instant.now());
    }

    public static SearchResult.SearchResultBuilder searchResult() {
        return SearchResult.builder()
                .url("https://example.com/test")
                .title("Test Result")
                .description("This is a test result")
                .rank(1)
                .clickCount(0);
    }

    public static List<SearchResultDTO> sampleResults(int count) {
        List<SearchResultDTO> results = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            results.add(searchResultDTO()
                    .url("https://example.com/result" + i)
                    .title("Result " + i)
                    .rank(i)
                    .build());
        }
        return results;
    }
}