package com.metamoteur.exception;

/**
 * Codes d'erreur standardisés
 */
public enum ErrorCodes {

    // Validation (1000-1999)
    VALIDATION_ERROR("1000", "Validation error"),
    INVALID_SEARCH_QUERY("1001", "Invalid search query"),
    INVALID_URL("1002", "Invalid URL"),
    INVALID_PARAMETER("1003", "Invalid parameter"),

    // Recherche (2000-2999)
    SEARCH_ERROR("2000", "Search error"),
    SEARCH_ENGINE_UNAVAILABLE("2001", "Search engine unavailable"),
    NO_RESULTS_FOUND("2002", "No results found"),
    PARSING_ERROR("2003", "Parsing error"),
    ALL_ENGINES_FAILED("2004", "All search engines failed"),

    // Agents (3000-3999)
    AGENT_COMMUNICATION_ERROR("3000", "Agent communication error"),
    AGENT_NOT_FOUND("3001", "Agent not found"),
    AGENT_TIMEOUT("3002", "Agent timeout"),

    // Base de données (4000-4999)
    DATABASE_ERROR("4000", "Database error"),
    RESOURCE_NOT_FOUND("4001", "Resource not found"),
    DUPLICATE_RESOURCE("4002", "Duplicate resource"),

    // Sécurité (5000-5999)
    SECURITY_VIOLATION("5000", "Security violation"),
    RATE_LIMIT_EXCEEDED("5001", "Rate limit exceeded"),
    UNAUTHORIZED("5002", "Unauthorized"),
    FORBIDDEN("5003", "Forbidden"),

    // Cache (6000-6999)
    CACHE_ERROR("6000", "Cache error"),

    // Système (9000-9999)
    INTERNAL_ERROR("9000", "Internal server error"),
    SERVICE_UNAVAILABLE("9001", "Service unavailable");

    private final String code;
    private final String description;

    ErrorCodes(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}