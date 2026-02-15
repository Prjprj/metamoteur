package com.metamoteur.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO pour les réponses d'erreur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String message;

    private String details;

    @Builder.Default
    private Instant timestamp = Instant.now();

    private String path;

    private Integer status;

    private String errorCode;

    private Map<String, String> validationErrors;
}