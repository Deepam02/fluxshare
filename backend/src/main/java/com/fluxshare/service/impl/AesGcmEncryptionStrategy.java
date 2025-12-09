package com.fluxshare.service.impl;

import com.fluxshare.exception.EncryptionException;
import com.fluxshare.service.EncryptionStrategy;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

/**
 * AES-GCM encryption strategy implementation.
 * Provides authenticated encryption with associated data (AEAD).
 */
@Component
public class AesGcmEncryptionStrategy implements EncryptionStrategy {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12; // 96 bits
    private static final int TAG_SIZE = 128; // 128 bits
    private static final int BUFFER_SIZE = 8192;

    private final SecureRandom secureRandom;

    public AesGcmEncryptionStrategy() {
        this.secureRandom = new SecureRandom();
    }

    @Override
    public byte[] encrypt(byte[] plainData, byte[] key) {
        try {
            // Generate IV
            byte[] iv = new byte[IV_SIZE];
            secureRandom.nextBytes(iv);

            // Create cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);

            // Encrypt
            byte[] encryptedData = cipher.doFinal(plainData);

            // Combine IV + encrypted data
            byte[] result = new byte[IV_SIZE + encryptedData.length];
            System.arraycopy(iv, 0, result, 0, IV_SIZE);
            System.arraycopy(encryptedData, 0, result, IV_SIZE, encryptedData.length);

            return result;
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, byte[] key) {
        try {
            if (encryptedData.length < IV_SIZE) {
                throw new EncryptionException("Invalid encrypted data: too short");
            }

            // Extract IV
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(encryptedData, 0, iv, 0, IV_SIZE);

            // Extract encrypted content
            byte[] cipherText = new byte[encryptedData.length - IV_SIZE];
            System.arraycopy(encryptedData, IV_SIZE, cipherText, 0, cipherText.length);

            // Create cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

            // Decrypt
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt data", e);
        }
    }

    @Override
    public void encryptStream(InputStream inputStream, OutputStream outputStream, byte[] key) {
        try {
            // Generate IV
            byte[] iv = new byte[IV_SIZE];
            secureRandom.nextBytes(iv);

            // Write IV first
            outputStream.write(iv);

            // Create cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);

            // Encrypt and write data
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    outputStream.write(output);
                }
            }

            // Write final block
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                outputStream.write(finalBlock);
            }

            outputStream.flush();
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt stream", e);
        }
    }

    @Override
    public void decryptStream(InputStream inputStream, OutputStream outputStream, byte[] key) {
        try {
            // Read IV
            byte[] iv = new byte[IV_SIZE];
            int ivBytesRead = inputStream.read(iv);
            if (ivBytesRead != IV_SIZE) {
                throw new EncryptionException("Failed to read IV from encrypted stream");
            }

            // Create cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

            // Decrypt and write data
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    outputStream.write(output);
                }
            }

            // Write final block
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                outputStream.write(finalBlock);
            }

            outputStream.flush();
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt stream", e);
        }
    }

    @Override
    public byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e) {
            throw new EncryptionException("Failed to generate encryption key", e);
        }
    }

    @Override
    public String getAlgorithmName() {
        return TRANSFORMATION;
    }
}
