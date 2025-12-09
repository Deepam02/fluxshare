package com.fluxshare.service;

import com.fluxshare.exception.EncryptionException;
import com.fluxshare.service.impl.AesGcmEncryptionStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * Service for encryption operations.
 * Uses Strategy pattern to allow different encryption algorithms.
 */
@Service
public class EncryptionService {

    private final EncryptionStrategy encryptionStrategy;
    private final byte[] masterKey;

    public EncryptionService(
            AesGcmEncryptionStrategy encryptionStrategy,
            @Value("${fluxshare.encryption.master-key}") String masterKeyString) {
        this.encryptionStrategy = encryptionStrategy;
        this.masterKey = deriveMasterKey(masterKeyString);
    }

    /**
     * Generate a new content encryption key
     * 
     * @return A new encryption key
     */
    public byte[] generateContentKey() {
        return encryptionStrategy.generateKey();
    }

    /**
     * Encrypt content key with master key (key wrapping)
     * 
     * @param contentKey The content key to wrap
     * @return Encrypted content key
     */
    public byte[] wrapContentKey(byte[] contentKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(masterKey, "AES");
            cipher.init(Cipher.WRAP_MODE, keySpec);
            return cipher.wrap(new SecretKeySpec(contentKey, "AES"));
        } catch (Exception e) {
            throw new EncryptionException("Failed to wrap content key", e);
        }
    }

    /**
     * Decrypt content key with master key (key unwrapping)
     * 
     * @param wrappedKey The wrapped content key
     * @return Decrypted content key
     */
    public byte[] unwrapContentKey(byte[] wrappedKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(masterKey, "AES");
            cipher.init(Cipher.UNWRAP_MODE, keySpec);
            return cipher.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY).getEncoded();
        } catch (Exception e) {
            throw new EncryptionException("Failed to unwrap content key", e);
        }
    }

    /**
     * Encrypt data using the configured strategy
     * 
     * @param plainData The data to encrypt
     * @param key The encryption key
     * @return Encrypted data
     */
    public byte[] encrypt(byte[] plainData, byte[] key) {
        return encryptionStrategy.encrypt(plainData, key);
    }

    /**
     * Decrypt data using the configured strategy
     * 
     * @param encryptedData The data to decrypt
     * @param key The encryption key
     * @return Decrypted data
     */
    public byte[] decrypt(byte[] encryptedData, byte[] key) {
        return encryptionStrategy.decrypt(encryptedData, key);
    }

    /**
     * Encrypt string data
     * 
     * @param plainText The text to encrypt
     * @param key The encryption key
     * @return Encrypted data
     */
    public byte[] encryptString(String plainText, byte[] key) {
        return encrypt(plainText.getBytes(), key);
    }

    /**
     * Decrypt to string
     * 
     * @param encryptedData The data to decrypt
     * @param key The encryption key
     * @return Decrypted text
     */
    public String decryptToString(byte[] encryptedData, byte[] key) {
        byte[] decrypted = decrypt(encryptedData, key);
        return new String(decrypted);
    }

    /**
     * Encrypt stream
     * 
     * @param inputStream Input stream with plain data
     * @param outputStream Output stream for encrypted data
     * @param key The encryption key
     */
    public void encryptStream(InputStream inputStream, OutputStream outputStream, byte[] key) {
        encryptionStrategy.encryptStream(inputStream, outputStream, key);
    }

    /**
     * Decrypt stream
     * 
     * @param inputStream Input stream with encrypted data
     * @param outputStream Output stream for decrypted data
     * @param key The encryption key
     */
    public void decryptStream(InputStream inputStream, OutputStream outputStream, byte[] key) {
        encryptionStrategy.decryptStream(inputStream, outputStream, key);
    }

    /**
     * Get the encryption algorithm name
     * 
     * @return Algorithm name
     */
    public String getAlgorithmName() {
        return encryptionStrategy.getAlgorithmName();
    }

    /**
     * Derive a 256-bit master key from the configuration string
     * 
     * @param masterKeyString The master key configuration
     * @return 256-bit key
     */
    private byte[] deriveMasterKey(String masterKeyString) {
        try {
            // Try to decode as Base64 first
            return Base64.getDecoder().decode(masterKeyString);
        } catch (IllegalArgumentException e) {
            // If not Base64, use SHA-256 hash of the string
            try {
                java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                return digest.digest(masterKeyString.getBytes());
            } catch (Exception ex) {
                throw new EncryptionException("Failed to derive master key", ex);
            }
        }
    }
}
