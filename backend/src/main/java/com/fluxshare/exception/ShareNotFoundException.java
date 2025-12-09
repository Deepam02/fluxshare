package com.fluxshare.exception;

/**
 * Exception thrown when a share is not found
 */
public class ShareNotFoundException extends FluxShareException {
    
    public ShareNotFoundException(String shareId) {
        super("Share not found: " + shareId);
    }
}
