package com.fluxshare.exception;

/**
 * Exception thrown when rate limit is exceeded
 */
public class RateLimitExceededException extends FluxShareException {
    
    public RateLimitExceededException() {
        super("Rate limit exceeded. Please try again later.");
    }
    
    public RateLimitExceededException(String message) {
        super(message);
    }
}
