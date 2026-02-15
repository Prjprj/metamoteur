package com.metamoteur.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour un résultat de recherche individuel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {

    private Long id;

    private String url;

    private String title;

    private String description;

    private Integer rank;

    private Integer clickCount;

    private Double score;

    private String source; // "google", "local", "agent"
}