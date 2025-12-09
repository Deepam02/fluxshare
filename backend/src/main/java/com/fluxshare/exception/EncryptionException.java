package com.fluxshare.exception;

/**
 * Exception thrown when encryption/decryption operations fail
 */
public class EncryptionException extends FluxShareException {
    
    public EncryptionException(String message) {
        super(message);
    }
    
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
