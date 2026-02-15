package com.metamoteur.exception;

import lombok.Getter;

/**
 * Exception levée quand une requête de recherche est invalide
 */
@Getter
public class InvalidSearchQueryException extends MetaMoteurException {

    public InvalidSearchQueryException(String message) {
        super("INVALID_SEARCH_QUERY", message);
    }

    public InvalidSearchQueryException(String message, Object... args) {
        super("INVALID_SEARCH_QUERY", message, args);
    }

    public static InvalidSearchQueryException emptyQuery() {
        return new InvalidSearchQueryException("Search query cannot be empty");
    }

    public static InvalidSearchQueryException tooShort(int minLength) {
        return new InvalidSearchQueryException(
                "Search query too short (minimum %d characters)",
                minLength
        );
    }

    public static InvalidSearchQueryException tooLong(int maxLength) {
        return new InvalidSearchQueryException(
                "Search query too long (maximum %d characters)",
                maxLength
        );
    }

    public static InvalidSearchQueryException invalidCharacters(String invalidChars) {
        return new InvalidSearchQueryException(
                "Search query contains invalid characters: %s",
                invalidChars
        );
    }

    public static InvalidSearchQueryException sqlInjectionDetected() {
        return new InvalidSearchQueryException(
                "Potential SQL injection detected in search query"
        );
    }
}