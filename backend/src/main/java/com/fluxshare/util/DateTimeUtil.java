package com.fluxshare.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 * Implements Singleton pattern.
 */
@Component
public class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get current timestamp
     * 
     * @return Current LocalDateTime
     */
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Calculate expiry time from now plus specified hours
     * 
     * @param hours Number of hours from now
     * @return Expiry timestamp
     */
    public LocalDateTime calculateExpiryTime(int hours) {
        return LocalDateTime.now().plusHours(hours);
    }

    /**
     * Calculate expiry time from now plus specified minutes
     * 
     * @param minutes Number of minutes from now
     * @return Expiry timestamp
     */
    public LocalDateTime calculateExpiryTimeInMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * Check if a timestamp has passed
     * 
     * @param timestamp The timestamp to check
     * @return true if timestamp is in the past
     */
    public boolean isExpired(LocalDateTime timestamp) {
        return LocalDateTime.now().isAfter(timestamp);
    }

    /**
     * Get time remaining until expiry
     * 
     * @param expiryTime The expiry timestamp
     * @return Duration until expiry (negative if already expired)
     */
    public Duration getTimeRemaining(LocalDateTime expiryTime) {
        return Duration.between(LocalDateTime.now(), expiryTime);
    }

    /**
     * Get human-readable time remaining
     * 
     * @param expiryTime The expiry timestamp
     * @return Human-readable string like "2 hours 30 minutes"
     */
    public String getTimeRemainingFormatted(LocalDateTime expiryTime) {
        Duration duration = getTimeRemaining(expiryTime);
        
        if (duration.isNegative()) {
            return "Expired";
        }

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" day").append(days > 1 ? "s" : "");
        }
        if (hours > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(hours).append(" hour").append(hours > 1 ? "s" : "");
        }
        if (minutes > 0 && days == 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
        }

        return sb.length() > 0 ? sb.toString() : "Less than a minute";
    }

    /**
     * Format timestamp for display
     * 
     * @param timestamp The timestamp to format
     * @return Formatted string
     */
    public String formatForDisplay(LocalDateTime timestamp) {
        return timestamp.format(DISPLAY_FORMATTER);
    }

    /**
     * Format timestamp as ISO string
     * 
     * @param timestamp The timestamp to format
     * @return ISO formatted string
     */
    public String formatAsIso(LocalDateTime timestamp) {
        return timestamp.format(ISO_FORMATTER);
    }

    /**
     * Parse ISO string to LocalDateTime
     * 
     * @param isoString The ISO formatted string
     * @return Parsed LocalDateTime
     */
    public LocalDateTime parseIso(String isoString) {
        return LocalDateTime.parse(isoString, ISO_FORMATTER);
    }

    /**
     * Validate expiry hours against limits
     * 
     * @param hours The requested expiry hours
     * @param maxHours The maximum allowed hours
     * @return Validated hours (capped at max)
     */
    public int validateExpiryHours(int hours, int maxHours) {
        if (hours <= 0) {
            return 24; // Default to 24 hours
        }
        return Math.min(hours, maxHours);
    }

    /**
     * Get timestamp N hours ago
     * 
     * @param hours Number of hours in the past
     * @return Timestamp
     */
    public LocalDateTime hoursAgo(int hours) {
        return LocalDateTime.now().minusHours(hours);
    }

    /**
     * Get timestamp N days ago
     * 
     * @param days Number of days in the past
     * @return Timestamp
     */
    public LocalDateTime daysAgo(int days) {
        return LocalDateTime.now().minusDays(days);
    }
}
