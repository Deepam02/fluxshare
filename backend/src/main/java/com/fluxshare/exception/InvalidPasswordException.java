package com.fluxshare.exception;

/**
 * Exception thrown when password validation fails
 */
public class InvalidPasswordException extends FluxShareException {
    
    public InvalidPasswordException() {
        super("Invalid password");
    }
}
