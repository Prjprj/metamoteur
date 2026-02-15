package com.metamoteur.exception;

import java.util.Map;

/**
 * Exception pour erreurs de validation
 */
public class ValidationException extends MetaMoteurException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.fieldErrors = Map.of();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super("VALIDATION_ERROR", message);
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(Map<String, String> fieldErrors) {
        super("VALIDATION_ERROR", "Validation failed");
        this.fieldErrors = fieldErrors;
    }

    public static ValidationException singleField(String field, String error) {
        return new ValidationException(Map.of(field, error));
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}