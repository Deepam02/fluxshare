package com.fluxshare.exception;

/**
 * Exception thrown when file storage operations fail
 */
public class FileStorageException extends FluxShareException {
    
    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
