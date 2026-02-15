package com.metamoteur.exception;

import com.metamoteur.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions
 * Transforme les exceptions en réponses HTTP appropriées
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * Validation des arguments de méthode (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed")
                .details("One or more fields have validation errors")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.BAD_REQUEST.value())
                .validationErrors(fieldErrors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Violation de contraintes (@Pattern, @Size, etc.)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request
    ) {
        log.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Constraint violation")
                .details(ex.getMessage())
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.BAD_REQUEST.value())
                .validationErrors(errors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Erreur de conversion de type
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request
    ) {
        log.warn("Type mismatch: {}", ex.getMessage());

        String error = String.format(
                "Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid parameter type")
                .details(error)
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Requête de recherche invalide
     */
    @ExceptionHandler(InvalidSearchQueryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSearchQuery(
            InvalidSearchQueryException ex,
            WebRequest request
    ) {
        log.warn("Invalid search query: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .details("Please check your search query and try again")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * URL invalide
     */
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(
            InvalidUrlException ex,
            WebRequest request
    ) {
        log.warn("Invalid URL: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .details("The provided URL is invalid or blocked")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Ressource non trouvée
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request
    ) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .details(String.format("Resource type: %s", ex.getResourceType()))
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Erreur de recherche
     */
    @ExceptionHandler(SearchException.class)
    public ResponseEntity<ErrorResponse> handleSearchException(
            SearchException ex,
            WebRequest request
    ) {
        log.error("Search error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Search failed")
                .details(shouldExposeDetails() ? ex.getMessage() : "An error occurred during search")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * Moteur de recherche indisponible
     */
    @ExceptionHandler(SearchEngineUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSearchEngineUnavailable(
            SearchEngineUnavailableException ex,
            WebRequest request
    ) {
        log.error("Search engine unavailable: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .details("The search service is temporarily unavailable. Please try again later.")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }

    /**
     * Erreur de communication avec agent
     */
    @ExceptionHandler(AgentCommunicationException.class)
    public ResponseEntity<ErrorResponse> handleAgentCommunication(
            AgentCommunicationException ex,
            WebRequest request
    ) {
        log.error("Agent communication error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Agent communication failed")
                .details(shouldExposeDetails() ? ex.getMessage() : "Failed to contact remote agent")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.BAD_GATEWAY.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(errorResponse);
    }

    /**
     * Rate limit dépassé
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException ex,
            WebRequest request
    ) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .details("Please wait before making more requests")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "60")
                .body(errorResponse);
    }

    /**
     * Violation de sécurité
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(
            SecurityException ex,
            WebRequest request
    ) {
        log.error("Security violation: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Security violation detected")
                .details("Your request has been blocked for security reasons")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.FORBIDDEN.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    /**
     * Erreur de base de données
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            DatabaseException ex,
            WebRequest request
    ) {
        log.error("Database error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Database error")
                .details(shouldExposeDetails() ? ex.getMessage() : "An internal error occurred")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * Toutes les autres exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request
    ) {
        log.error("Unexpected error", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .details(shouldExposeDetails() ? ex.getMessage() : "Please try again later")
                .timestamp(Instant.now())
                .path(getRequestPath(request))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * Détermine si on doit exposer les détails (seulement en dev)
     */
    private boolean shouldExposeDetails() {
        return "dev".equals(activeProfile);
    }

    /**
     * Extrait le chemin de la requête
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}