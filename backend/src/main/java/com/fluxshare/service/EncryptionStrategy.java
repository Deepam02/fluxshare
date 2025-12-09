package com.fluxshare.service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Strategy interface for encryption operations.
 * Allows different encryption algorithms to be implemented.
 */
public interface EncryptionStrategy {

    /**
     * Encrypt data
     * 
     * @param plainData The plain data to encrypt
     * @param key The encryption key
     * @return Encrypted data
     */
    byte[] encrypt(byte[] plainData, byte[] key);

    /**
     * Decrypt data
     * 
     * @param encryptedData The encrypted data
     * @param key The encryption key
     * @return Decrypted plain data
     */
    byte[] decrypt(byte[] encryptedData, byte[] key);

    /**
     * Encrypt stream
     * 
     * @param inputStream The input stream to encrypt
     * @param outputStream The output stream for encrypted data
     * @param key The encryption key
     */
    void encryptStream(InputStream inputStream, OutputStream outputStream, byte[] key);

    /**
     * Decrypt stream
     * 
     * @param inputStream The encrypted input stream
     * @param outputStream The output stream for decrypted data
     * @param key The encryption key
     */
    void decryptStream(InputStream inputStream, OutputStream outputStream, byte[] key);

    /**
     * Generate a new encryption key
     * 
     * @return A new encryption key
     */
    byte[] generateKey();

    /**
     * Get the algorithm name
     * 
     * @return The name of the encryption algorithm
     */
    String getAlgorithmName();
}
