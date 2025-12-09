package com.fluxshare.service;

import com.fluxshare.dto.CodeShareRequest;
import com.fluxshare.dto.TextShareRequest;
import com.fluxshare.entity.Share;
import com.fluxshare.entity.TextContent;
import com.fluxshare.enums.ShareType;
import com.fluxshare.exception.FileStorageException;
import com.fluxshare.repository.TextContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing text and code content
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TextContentService {

    private final TextContentRepository textContentRepository;
    private final EncryptionService encryptionService;
    private final ShareService shareService;

    /**
     * Create text share
     */
    @Transactional
    public Share createTextShare(TextShareRequest request) {
        // Create share entity
        Share share = shareService.createShare(
                ShareType.TEXT,
                request.getExpiryHours(),
                request.getViewOnce(),
                request.getPassword(),
                request.getNotes(),
                null,
                request.getMaxViews()
        );

        // Get content key
        byte[] contentKey = shareService.getContentKey(share);

        // Encrypt and store text content
        byte[] encryptedContent = encryptionService.encryptString(request.getText(), contentKey);

        TextContent textContent = TextContent.builder()
                .share(share)
                .contentEncrypted(encryptedContent)
                .contentLength(request.getText().length())
                .isCode(false)
                .build();

        textContentRepository.save(textContent);
        log.info("Created text share: {}", share.getShareId());

        return share;
    }

    /**
     * Create code share
     */
    @Transactional
    public Share createCodeShare(CodeShareRequest request) {
        // Create share entity
        Share share = shareService.createShare(
                ShareType.CODE,
                request.getExpiryHours(),
                request.getViewOnce(),
                request.getPassword(),
                request.getNotes(),
                null,
                request.getMaxViews()
        );

        // Get content key
        byte[] contentKey = shareService.getContentKey(share);

        // Encrypt and store code content
        byte[] encryptedContent = encryptionService.encryptString(request.getCode(), contentKey);

        TextContent textContent = TextContent.builder()
                .share(share)
                .contentEncrypted(encryptedContent)
                .language(request.getLanguage())
                .contentLength(request.getCode().length())
                .isCode(true)
                .build();

        textContentRepository.save(textContent);
        log.info("Created code share: {} with language {}", share.getShareId(), request.getLanguage());

        return share;
    }

    /**
     * Get decrypted text content
     */
    public String getTextContent(Share share, byte[] contentKey) {
        TextContent textContent = textContentRepository.findByShare(share)
                .orElseThrow(() -> new FileStorageException("Text content not found for share: " + share.getShareId()));

        return encryptionService.decryptToString(textContent.getContentEncrypted(), contentKey);
    }

    /**
     * Get text content entity
     */
    public TextContent getTextContentEntity(Share share) {
        return textContentRepository.findByShare(share)
                .orElseThrow(() -> new FileStorageException("Text content not found for share: " + share.getShareId()));
    }

    /**
     * Delete text content for a share
     */
    @Transactional
    public void deleteTextContent(Share share) {
        textContentRepository.deleteByShare(share);
        log.info("Deleted text content for share: {}", share.getShareId());
    }
}
