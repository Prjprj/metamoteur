package com.metamoteur.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO pour réponses paginées
 */
@Data
@Builder
public class PagedSearchResponse {

    private String query;

    private List<SearchResultDTO> results;

    private Integer page;

    private Integer pageSize;

    private Long totalResults;

    private Integer totalPages;

    private Boolean hasNext;

    private Boolean hasPrevious;

    public static PagedSearchResponse from(
            String query,
            List<SearchResultDTO> allResults,
            int page,
            int pageSize
    ) {
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allResults.size());

        List<SearchResultDTO> pageResults = start < allResults.size()
                ? allResults.subList(start, end)
                : List.of();

        int totalPages = (int) Math.ceil((double) allResults.size() / pageSize);

        return PagedSearchResponse.builder()
                .query(query)
                .results(pageResults)
                .page(page)
                .pageSize(pageSize)
                .totalResults((long) allResults.size())
                .totalPages(totalPages)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();
    }
}