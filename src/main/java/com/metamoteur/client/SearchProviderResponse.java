package com.metamoteur.client;

import com.metamoteur.dto.SearchResultDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Réponse d'un provider de recherche
 */
@Data
@Builder
public class SearchProviderResponse {
    private String providerName;
    private List<SearchResultDTO> results;
    private boolean success;
    private String errorMessage;
    private long responseTimeMs;
}