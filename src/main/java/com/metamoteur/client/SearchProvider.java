package com.metamoteur.client;

import com.metamoteur.dto.SearchResultDTO;

import java.util.List;

/**
 * Interface pour les providers de recherche
 * Pattern Strategy pour multi-sources
 */
public interface SearchProvider {

    /**
     * Effectue une recherche
     */
    List<SearchResultDTO> search(String query, int maxResults);

    /**
     * Nom du provider
     */
    String getProviderName();

    /**
     * Priorité du provider (1 = highest)
     */
    int getPriority();

    /**
     * Vérifie si le provider est disponible
     */
    boolean isAvailable();
}