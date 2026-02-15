package com.metamoteur.exception;

import lombok.Getter;

/**
 * Exception de base pour toutes les exceptions métier
 */
@Getter
public class MetaMoteurException extends RuntimeException {

    private final String errorCode;
    private final Object[] args;

    public MetaMoteurException(String message) {
        super(message);
        this.errorCode = "METAMOTEUR_ERROR";
        this.args = new Object[0];
    }

    public MetaMoteurException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "METAMOTEUR_ERROR";
        this.args = new Object[0];
    }

    public MetaMoteurException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public MetaMoteurException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public MetaMoteurException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
}