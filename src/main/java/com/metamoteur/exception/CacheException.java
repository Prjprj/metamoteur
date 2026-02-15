package com.metamoteur.exception;

/**
 * Exception levée pour erreurs de cache
 */
public class CacheException extends MetaMoteurException {

    public CacheException(String message) {
        super("CACHE_ERROR", message);
    }

    public CacheException(String message, Throwable cause) {
        super("CACHE_ERROR", message, cause);
    }

    public static CacheException readFailed(String key, Throwable cause) {
        return new CacheException(
                String.format("Failed to read from cache for key: %s", key),
                cause
        );
    }

    public static CacheException writeFailed(String key, Throwable cause) {
        return new CacheException(
                String.format("Failed to write to cache for key: %s", key),
                cause
        );
    }

    public static CacheException invalidationFailed(Throwable cause) {
        return new CacheException("Cache invalidation failed", cause);
    }
}