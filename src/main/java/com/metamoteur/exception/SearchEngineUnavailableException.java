package com.metamoteur.exception;

/**
 * Exception levée quand un moteur de recherche est indisponible
 */
public class SearchEngineUnavailableException extends MetaMoteurException {

    private final String engineName;

    public SearchEngineUnavailableException(String engineName) {
        super(
                "SEARCH_ENGINE_UNAVAILABLE",
                String.format("Search engine '%s' is currently unavailable", engineName)
        );
        this.engineName = engineName;
    }

    public SearchEngineUnavailableException(String engineName, Throwable cause) {
        super(
                "SEARCH_ENGINE_UNAVAILABLE",
                String.format("Search engine '%s' is currently unavailable", engineName),
                cause
        );
        this.engineName = engineName;
    }

    public String getEngineName() {
        return engineName;
    }
}