package com.fluxshare.service;

import com.fluxshare.entity.FileMetadata;
import com.fluxshare.entity.Share;
import com.fluxshare.exception.FileStorageException;
import com.fluxshare.repository.FileMetadataRepository;
import com.fluxshare.util.MimeTypeUtil;
import com.fluxshare.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for file storage operations with streaming support.
 * Implements Template Method pattern for file streaming workflows.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final EncryptionService encryptionService;
    private final FileMetadataRepository fileMetadataRepository;
    private final MimeTypeUtil mimeTypeUtil;
    private final ZipUtil zipUtil;

    @Value("${fluxshare.storage.base-path:./storage/encrypted}")
    private String basePath;

    @Value("${fluxshare.storage.temp-path:./storage/temp}")
    private String tempPath;

    /**
     * Initialize storage directories
     */
    public void initializeStorage() {
        try {
            Files.createDirectories(Paths.get(basePath));
            Files.createDirectories(Paths.get(tempPath));
            log.info("Storage directories initialized: {} and {}", basePath, tempPath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to initialize storage directories", e);
        }
    }

    /**
     * Store an encrypted file
     */
    @Transactional
    public FileMetadata storeFile(MultipartFile file, Share share, byte[] contentKey) {
        try {
            // Generate unique filename
            String storedFilename = generateStoredFilename(share.getShareId(), file.getOriginalFilename());
            Path storedPath = Paths.get(basePath, storedFilename);

            // Ensure parent directory exists
            Files.createDirectories(storedPath.getParent());

            // Encrypt and store file using streaming
            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new FileOutputStream(storedPath.toFile())) {
                
                encryptionService.encryptStream(inputStream, outputStream, contentKey);
            }

            // Detect MIME type
            String mimeType = mimeTypeUtil.detectFromFilename(file.getOriginalFilename());

            // Create metadata
            FileMetadata metadata = FileMetadata.builder()
                    .share(share)
                    .filename(file.getOriginalFilename())
                    .storedPath(storedPath.toString())
                    .mimeType(mimeType)
                    .size(file.getSize())
                    .isPreviewable(mimeTypeUtil.isPreviewable(mimeType))
                    .build();

            metadata = fileMetadataRepository.save(metadata);
            log.info("Stored file: {} for share {}", file.getOriginalFilename(), share.getShareId());

            return metadata;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Store multiple files
     */
    @Transactional
    public List<FileMetadata> storeFiles(List<MultipartFile> files, Share share, byte[] contentKey) {
        List<FileMetadata> metadataList = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                FileMetadata metadata = storeFile(file, share, contentKey);
                metadataList.add(metadata);
            }
        }
        
        log.info("Stored {} files for share {}", metadataList.size(), share.getShareId());
        return metadataList;
    }

    /**
     * Stream decrypted file to output stream (Template Method pattern)
     */
    public void streamFile(FileMetadata fileMetadata, OutputStream outputStream, byte[] contentKey) {
        try {
            Path filePath = Paths.get(fileMetadata.getStoredPath());
            
            if (!Files.exists(filePath)) {
                throw new FileStorageException("File not found: " + fileMetadata.getFilename());
            }

            try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
                encryptionService.decryptStream(inputStream, outputStream, contentKey);
            }

            log.debug("Streamed file: {}", fileMetadata.getFilename());
        } catch (IOException e) {
            throw new FileStorageException("Failed to stream file: " + fileMetadata.getFilename(), e);
        }
    }

    /**
     * Get decrypted file as byte array (for small files)
     */
    public byte[] getFileBytes(FileMetadata fileMetadata, byte[] contentKey) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        streamFile(fileMetadata, outputStream, contentKey);
        return outputStream.toByteArray();
    }

    /**
     * Create ZIP file with all files from a share
     */
    public void createZipForShare(Share share, OutputStream outputStream, byte[] contentKey) {
        try {
            List<FileMetadata> files = fileMetadataRepository.findByShare(share);
            
            if (files.isEmpty()) {
                throw new FileStorageException("No files found for share: " + share.getShareId());
            }

            // Create list of file data for ZIP
            List<ZipUtil.FileData> fileDataList = new ArrayList<>();
            
            for (FileMetadata file : files) {
                // Create decrypted input stream for each file
                ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream();
                streamFile(file, decryptedStream, contentKey);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(decryptedStream.toByteArray());
                
                fileDataList.add(new ZipUtil.FileData(file.getFilename(), inputStream));
            }

            // Create ZIP
            zipUtil.createZipFromStreams(fileDataList, outputStream);
            
            log.info("Created ZIP with {} files for share {}", files.size(), share.getShareId());
        } catch (IOException e) {
            throw new FileStorageException("Failed to create ZIP", e);
        }
    }

    /**
     * Delete file from storage
     */
    public void deleteFile(FileMetadata fileMetadata) {
        try {
            Path filePath = Paths.get(fileMetadata.getStoredPath());
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.debug("Deleted file: {}", fileMetadata.getFilename());
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileMetadata.getFilename(), e);
        }
    }

    /**
     * Delete all files for a share
     */
    @Transactional
    public void deleteFilesForShare(Share share) {
        List<FileMetadata> files = fileMetadataRepository.findByShare(share);
        
        for (FileMetadata file : files) {
            deleteFile(file);
        }
        
        fileMetadataRepository.deleteByShare(share);
        log.info("Deleted {} files for share {}", files.size(), share.getShareId());
    }

    /**
     * Get preview for a file (limited bytes)
     */
    public byte[] getFilePreview(FileMetadata fileMetadata, byte[] contentKey, int maxBytes) {
        if (!fileMetadata.canPreview()) {
            throw new FileStorageException("File is not previewable: " + fileMetadata.getFilename());
        }

        try {
            Path filePath = Paths.get(fileMetadata.getStoredPath());
            
            if (!Files.exists(filePath)) {
                throw new FileStorageException("File not found: " + fileMetadata.getFilename());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
                encryptionService.decryptStream(inputStream, outputStream, contentKey);
            }

            byte[] fullContent = outputStream.toByteArray();
            
            // Return only first maxBytes
            if (fullContent.length <= maxBytes) {
                return fullContent;
            }
            
            byte[] preview = new byte[maxBytes];
            System.arraycopy(fullContent, 0, preview, 0, maxBytes);
            return preview;
            
        } catch (IOException e) {
            throw new FileStorageException("Failed to generate preview: " + fileMetadata.getFilename(), e);
        }
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(FileMetadata fileMetadata) {
        Path filePath = Paths.get(fileMetadata.getStoredPath());
        return Files.exists(filePath);
    }

    /**
     * Get file by name from share
     */
    public FileMetadata getFileByName(Share share, String filename) {
        return fileMetadataRepository.findByShareAndFilename(share, filename)
                .orElseThrow(() -> new FileStorageException("File not found: " + filename));
    }

    /**
     * Get all files for a share
     */
    public List<FileMetadata> getFilesForShare(Share share) {
        return fileMetadataRepository.findByShare(share);
    }

    /**
     * Generate a unique stored filename
     */
    private String generateStoredFilename(String shareId, String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFilename);
        return shareId + "_" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1);
    }
}
