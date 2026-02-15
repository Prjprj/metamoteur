package com.metamoteur.exception;

/**
 * Exception levée lors d'erreurs de parsing HTML/XML
 */
public class ParsingException extends MetaMoteurException {

    public ParsingException(String message) {
        super("PARSING_ERROR", message);
    }

    public ParsingException(String message, Throwable cause) {
        super("PARSING_ERROR", message, cause);
    }

    public static ParsingException htmlParsingFailed(String url, Throwable cause) {
        return new ParsingException(
                String.format("Failed to parse HTML from URL: %s", url),
                cause
        );
    }

    public static ParsingException invalidHtmlStructure(String url) {
        return new ParsingException(
                String.format("Invalid HTML structure at URL: %s", url)
        );
    }

    public static ParsingException selectorNotFound(String selector) {
        return new ParsingException(
                String.format("CSS selector not found: %s", selector)
        );
    }
}