package com.metamoteur.exception;

/**
 * Exception levée pour erreurs de base de données
 */
public class DatabaseException extends MetaMoteurException {

    public DatabaseException(String message) {
        super("DATABASE_ERROR", message);
    }

    public DatabaseException(String message, Throwable cause) {
        super("DATABASE_ERROR", message, cause);
    }

    public static DatabaseException connectionFailed(Throwable cause) {
        return new DatabaseException("Database connection failed", cause);
    }

    public static DatabaseException queryFailed(String query, Throwable cause) {
        return new DatabaseException(
                String.format("Query execution failed: %s", query),
                cause
        );
    }

    public static DatabaseException transactionFailed(Throwable cause) {
        return new DatabaseException("Transaction failed", cause);
    }
}