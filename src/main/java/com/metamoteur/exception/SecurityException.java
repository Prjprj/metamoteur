package com.metamoteur.exception;

/**
 * Exception pour violations de sécurité
 */
public class SecurityException extends MetaMoteurException {

    public SecurityException(String message) {
        super("SECURITY_VIOLATION", message);
    }

    public SecurityException(String message, Throwable cause) {
        super("SECURITY_VIOLATION", message, cause);
    }

    public static SecurityException xssDetected(String input) {
        return new SecurityException(
                String.format("Potential XSS attack detected in input: %s", input)
        );
    }

    public static SecurityException sqlInjectionDetected(String input) {
        return new SecurityException(
                String.format("Potential SQL injection detected: %s", input)
        );
    }

    public static SecurityException unauthorizedAccess(String resource) {
        return new SecurityException(
                String.format("Unauthorized access to resource: %s", resource)
        );
    }
}