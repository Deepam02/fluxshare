package com.fluxshare.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility class for password hashing and validation.
 * Uses BCrypt algorithm for secure password hashing.
 * Implements Singleton pattern.
 */
@Component
public class PasswordHashUtil {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordHashUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Hash a plain text password
     * 
     * @param plainPassword The plain text password
     * @return The hashed password
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verify a plain text password against a hashed password
     * 
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Check if a password meets minimum security requirements
     * 
     * @param password The password to validate
     * @return true if password meets requirements
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 4) {
            return false;
        }
        // Add more complex validation rules if needed
        return true;
    }
}
