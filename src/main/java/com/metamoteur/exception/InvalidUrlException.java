package com.metamoteur.exception;

/**
 * Exception levée pour une URL invalide
 */
public class InvalidUrlException extends MetaMoteurException {

    public InvalidUrlException(String message) {
        super("INVALID_URL", message);
    }

    public InvalidUrlException(String url, String reason) {
        super("INVALID_URL", String.format("Invalid URL '%s': %s", url, reason));
    }

    public static InvalidUrlException malformed(String url) {
        return new InvalidUrlException(url, "Malformed URL syntax");
    }

    public static InvalidUrlException localAddressBlocked(String url) {
        return new InvalidUrlException(url, "Local/internal addresses are blocked");
    }

    public static InvalidUrlException protocolNotAllowed(String url, String protocol) {
        return new InvalidUrlException(
                url,
                String.format("Protocol '%s' not allowed", protocol)
        );
    }

    public static InvalidUrlException domainBlocked(String url, String domain) {
        return new InvalidUrlException(
                url,
                String.format("Domain '%s' is blocked", domain)
        );
    }
}