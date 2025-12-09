package com.fluxshare.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Utility class for generating unique share IDs.
 * Implements Singleton pattern.
 */
@Component
public class IdGeneratorUtil {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new SecureRandom();

    /**
     * Generate a random alphanumeric ID of specified length
     * 
     * @param length The length of the ID to generate
     * @return A random alphanumeric string
     */
    public String generateId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Generate a random share ID with default length of 8 characters
     * 
     * @return A random 8-character alphanumeric string
     */
    public String generateShareId() {
        return generateId(8);
    }

    /**
     * Generate a URL-safe random ID
     * 
     * @param length The length of the ID
     * @return A URL-safe random string
     */
    public String generateUrlSafeId(int length) {
        String urlSafeChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(urlSafeChars.charAt(RANDOM.nextInt(urlSafeChars.length())));
        }
        return sb.toString();
    }
}
