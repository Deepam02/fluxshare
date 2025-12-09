package com.fluxshare.exception;

/**
 * Base exception for all FluxShare application exceptions
 */
public class FluxShareException extends RuntimeException {
    
    public FluxShareException(String message) {
        super(message);
    }
    
    public FluxShareException(String message, Throwable cause) {
        super(message, cause);
    }
}
