package com.fluxshare.service;

import com.fluxshare.exception.RateLimitExceededException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service for rate limiting share access
 */
@Service
@Slf4j
public class RateLimitService {

    @Value("${fluxshare.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${fluxshare.rate-limit.requests-per-minute:10}")
    private int requestsPerMinute;

    private final LoadingCache<String, RateLimiter> limiters;

    public RateLimitService() {
        this.limiters = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build(new CacheLoader<String, RateLimiter>() {
                    @Override
                    public RateLimiter load(String key) {
                        // Create rate limiter for this key
                        return RateLimiter.create(requestsPerMinute / 60.0); // per second
                    }
                });
    }

    /**
     * Check if request should be allowed
     * 
     * @param shareId The share being accessed
     * @param clientIp The client IP address
     * @throws RateLimitExceededException if rate limit exceeded
     */
    public void checkRateLimit(String shareId, String clientIp) {
        if (!rateLimitEnabled) {
            return;
        }

        String key = shareId + ":" + clientIp;
        
        try {
            RateLimiter limiter = limiters.get(key);
            if (!limiter.tryAcquire()) {
                log.warn("Rate limit exceeded for share {} from IP {}", shareId, clientIp);
                throw new RateLimitExceededException();
            }
        } catch (ExecutionException e) {
            log.error("Error checking rate limit", e);
            // Fail open - allow request if rate limiting fails
        }
    }

    /**
     * Check rate limit with custom message
     */
    public void checkRateLimit(String shareId, String clientIp, String customMessage) {
        if (!rateLimitEnabled) {
            return;
        }

        String key = shareId + ":" + clientIp;
        
        try {
            RateLimiter limiter = limiters.get(key);
            if (!limiter.tryAcquire()) {
                log.warn("Rate limit exceeded: {} for share {} from IP {}", 
                        customMessage, shareId, clientIp);
                throw new RateLimitExceededException(customMessage);
            }
        } catch (ExecutionException e) {
            log.error("Error checking rate limit", e);
        }
    }

    /**
     * Clear rate limit for a key (for testing or admin purposes)
     */
    public void clearRateLimit(String shareId, String clientIp) {
        String key = shareId + ":" + clientIp;
        limiters.invalidate(key);
    }

    /**
     * Clear all rate limits
     */
    public void clearAllRateLimits() {
        limiters.invalidateAll();
    }
}
