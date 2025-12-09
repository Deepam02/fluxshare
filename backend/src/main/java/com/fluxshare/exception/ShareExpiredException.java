package com.fluxshare.exception;

/**
 * Exception thrown when a share has expired
 */
public class ShareExpiredException extends FluxShareException {
    
    public ShareExpiredException(String shareId) {
        super("Share has expired: " + shareId);
    }
}
