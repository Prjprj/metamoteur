package com.metamoteur.exception;

/**
 * Exception levée lors d'erreurs pendant la recherche
 */
public class SearchException extends MetaMoteurException {

    public SearchException(String message) {
        super("SEARCH_ERROR", message);
    }

    public SearchException(String message, Throwable cause) {
        super("SEARCH_ERROR", message, cause);
    }

    public static SearchException engineFailure(String engineName, Throwable cause) {
        return new SearchException(
                String.format("Search engine '%s' failed", engineName),
                cause
        );
    }

    public static SearchException timeout(String engineName) {
        return new SearchException(
                String.format("Search engine '%s' timed out", engineName)
        );
    }

    public static SearchException noResultsFound(String query) {
        return new SearchException(
                String.format("No results found for query: %s", query)
        );
    }

    public static SearchException allEnginesFailed() {
        return new SearchException("All search engines failed to respond");
    }
}