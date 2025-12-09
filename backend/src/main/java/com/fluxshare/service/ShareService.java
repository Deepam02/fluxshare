package com.fluxshare.service;

import com.fluxshare.dto.*;
import com.fluxshare.entity.FileMetadata;
import com.fluxshare.entity.Share;
import com.fluxshare.entity.TextContent;
import com.fluxshare.enums.ShareType;
import com.fluxshare.exception.InvalidPasswordException;
import com.fluxshare.exception.ShareExpiredException;
import com.fluxshare.exception.ShareNotFoundException;
import com.fluxshare.repository.FileMetadataRepository;
import com.fluxshare.repository.ShareRepository;
import com.fluxshare.repository.TextContentRepository;
import com.fluxshare.util.DateTimeUtil;
import com.fluxshare.util.IdGeneratorUtil;
import com.fluxshare.util.PasswordHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing shares
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShareService {

    private final ShareRepository shareRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final TextContentRepository textContentRepository;
    private final EncryptionService encryptionService;
    private final IdGeneratorUtil idGeneratorUtil;
    private final PasswordHashUtil passwordHashUtil;
    private final DateTimeUtil dateTimeUtil;

    @Value("${fluxshare.share.default-expiry-hours:24}")
    private int defaultExpiryHours;

    @Value("${fluxshare.share.max-expiry-hours:168}")
    private int maxExpiryHours;

    /**
     * Create a new share entity
     */
    @Transactional
    public Share createShare(ShareType type, Integer expiryHours, Boolean viewOnce, 
                            String password, String notes, Integer maxDownloads, 
                            Integer maxViews) {
        // Generate unique share ID
        String shareId = generateUniqueShareId();

        // Calculate expiry time
        int validatedHours = dateTimeUtil.validateExpiryHours(
                expiryHours != null ? expiryHours : defaultExpiryHours, 
                maxExpiryHours
        );
        LocalDateTime expiryTime = dateTimeUtil.calculateExpiryTime(validatedHours);

        // Generate and wrap content encryption key
        byte[] contentKey = encryptionService.generateContentKey();
        byte[] wrappedKey = encryptionService.wrapContentKey(contentKey);

        // Hash password if provided
        String passwordHash = password != null && !password.isEmpty() 
                ? passwordHashUtil.hashPassword(password) 
                : null;

        // Build share entity
        Share share = Share.builder()
                .shareId(shareId)
                .type(type)
                .expiryTime(expiryTime)
                .viewOnce(viewOnce != null ? viewOnce : false)
                .passwordHash(passwordHash)
                .encryptedKey(wrappedKey)
                .notes(notes)
                .maxDownloads(maxDownloads)
                .maxViews(maxViews)
                .build();

        share = shareRepository.save(share);
        log.info("Created new share: {} of type {}", shareId, type);

        return share;
    }

    /**
     * Get share by ID with validation
     */
    @Transactional
    public Share getShareById(String shareId) {
        Share share = shareRepository.findByShareId(shareId)
                .orElseThrow(() -> new ShareNotFoundException(shareId));

        validateShare(share);
        return share;
    }

    /**
     * Get share by ID without expiry validation (for metadata)
     */
    public Share getShareByIdWithoutValidation(String shareId) {
        return shareRepository.findByShareId(shareId)
                .orElseThrow(() -> new ShareNotFoundException(shareId));
    }

    /**
     * Validate share (expiry and view-once)
     */
    public void validateShare(Share share) {
        if (share.isExpired()) {
            log.warn("Attempted to access expired share: {}", share.getShareId());
            throw new ShareExpiredException(share.getShareId());
        }
    }

    /**
     * Validate password for a share
     */
    public void validatePassword(Share share, String password) {
        if (share.isPasswordProtected()) {
            if (password == null || password.isEmpty()) {
                throw new InvalidPasswordException();
            }
            if (!passwordHashUtil.verifyPassword(password, share.getPasswordHash())) {
                log.warn("Invalid password attempt for share: {}", share.getShareId());
                throw new InvalidPasswordException();
            }
        }
    }

    /**
     * Handle view-once logic with thread-safe operation
     */
    @Transactional
    public synchronized void handleViewOnce(Share share) {
        if (share.getViewOnce()) {
            log.info("Marking view-once share as expired: {}", share.getShareId());
            share.setHasExpired(true);
            shareRepository.save(share);
        }
    }

    /**
     * Increment view count
     */
    @Transactional
    public void incrementViewCount(Share share) {
        share.incrementViewCount();
        shareRepository.save(share);
        
        // Check if max views reached
        if (share.getMaxViews() != null && share.getViewCount() >= share.getMaxViews()) {
            share.setHasExpired(true);
            shareRepository.save(share);
            log.info("Share {} reached max views and is now expired", share.getShareId());
        }
    }

    /**
     * Increment download count
     */
    @Transactional
    public void incrementDownloadCount(Share share) {
        share.incrementDownloadCount();
        shareRepository.save(share);
        
        // Check if max downloads reached
        if (share.getMaxDownloads() != null && share.getDownloadCount() >= share.getMaxDownloads()) {
            share.setHasExpired(true);
            shareRepository.save(share);
            log.info("Share {} reached max downloads and is now expired", share.getShareId());
        }
    }

    /**
     * Delete a share
     */
    @Transactional
    public void deleteShare(String shareId) {
        Share share = getShareByIdWithoutValidation(shareId);
        shareRepository.delete(share);
        log.info("Deleted share: {}", shareId);
    }

    /**
     * Get metadata for a share
     */
    public ShareMetadataResponse getMetadata(String shareId) {
        Share share = getShareByIdWithoutValidation(shareId);

        ShareMetadataResponse.ShareMetadataResponseBuilder builder = ShareMetadataResponse.builder()
                .shareId(share.getShareId())
                .type(share.getType().name())
                .expiryTime(share.getExpiryTime())
                .timeRemaining(dateTimeUtil.getTimeRemainingFormatted(share.getExpiryTime()))
                .viewOnce(share.getViewOnce())
                .passwordProtected(share.isPasswordProtected())
                .createdAt(share.getCreatedAt())
                .notes(share.getNotes())
                .viewCount(share.getViewCount())
                .downloadCount(share.getDownloadCount())
                .maxDownloads(share.getMaxDownloads())
                .maxViews(share.getMaxViews());

        // Add file information if file share
        if (share.getType() == ShareType.FILE) {
            List<FileMetadata> files = fileMetadataRepository.findByShare(share);
            List<ShareMetadataResponse.FileInfo> fileInfos = files.stream()
                    .map(f -> ShareMetadataResponse.FileInfo.builder()
                            .name(f.getFilename())
                            .size(f.getSize())
                            .mimeType(f.getMimeType())
                            .previewable(f.canPreview())
                            .build())
                    .collect(Collectors.toList());
            builder.files(fileInfos);
        }

        return builder.build();
    }

    /**
     * Get link preview for a share
     */
    public LinkPreviewResponse getLinkPreview(String shareId) {
        Share share = getShareByIdWithoutValidation(shareId);
        String timeRemaining = dateTimeUtil.getTimeRemainingFormatted(share.getExpiryTime());

        return switch (share.getType()) {
            case FILE -> {
                int fileCount = (int) fileMetadataRepository.countByShare(share);
                yield LinkPreviewResponse.forFileShare(fileCount, timeRemaining, 
                        share.isPasswordProtected());
            }
            case TEXT -> LinkPreviewResponse.forTextShare(timeRemaining, 
                    share.isPasswordProtected());
            case CODE -> {
                TextContent content = textContentRepository.findByShare(share).orElse(null);
                String language = content != null ? content.getLanguage() : null;
                yield LinkPreviewResponse.forCodeShare(language, timeRemaining, 
                        share.isPasswordProtected());
            }
        };
    }

    /**
     * Get unwrapped content key for a share
     */
    public byte[] getContentKey(Share share) {
        return encryptionService.unwrapContentKey(share.getEncryptedKey());
    }

    /**
     * Generate a unique share ID
     */
    private String generateUniqueShareId() {
        String shareId;
        do {
            shareId = idGeneratorUtil.generateShareId();
        } while (shareRepository.existsByShareId(shareId));
        return shareId;
    }
}
