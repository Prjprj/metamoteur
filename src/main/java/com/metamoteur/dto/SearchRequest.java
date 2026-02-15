package com.metamoteur.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les requêtes de recherche entrantes
 * Validation stricte pour prévenir les injections
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    @NotBlank(message = "La requête ne peut pas être vide")
    @Size(min = 2, max = 500, message = "La requête doit contenir entre 2 et 500 caractères")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}\\s\\-_.,']+$",
            message = "La requête contient des caractères non autorisés"
    )
    private String query;

    @Min(value = 1, message = "Le nombre minimum de résultats est 1")
    @Max(value = 100, message = "Le nombre maximum de résultats est 100")
    @Builder.Default
    private Integer maxResults = 20;

    @Pattern(
            regexp = "^(google|bing|duckduckgo|local)$",
            message = "Moteur de recherche non supporté"
    )
    @Builder.Default
    private String engine = "google";

    @Builder.Default
    private Boolean useCache = true;

    @Builder.Default
    private Boolean contactAgents = true;

    public Boolean isApplyLocalPermutation() {
        return true;
    }
}