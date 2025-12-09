package com.fluxshare.service;

import com.fluxshare.dto.FileShareRequest;
import com.fluxshare.entity.FileMetadata;
import com.fluxshare.entity.Share;
import com.fluxshare.enums.ShareType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Factory service for creating different types of shares.
 * Implements Factory pattern.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShareFactoryService {

    private final ShareService shareService;
    private final FileStorageService fileStorageService;
    private final TextContentService textContentService;

    /**
     * Create a file share
     */
    @Transactional
    public Share createFileShare(List<MultipartFile> files, FileShareRequest request) {
        // Create share entity
        Share share = shareService.createShare(
                ShareType.FILE,
                request.getExpiryHours(),
                request.getViewOnce(),
                request.getPassword(),
                request.getNotes(),
                request.getMaxDownloads(),
                request.getMaxViews()
        );

        // Get content key
        byte[] contentKey = shareService.getContentKey(share);

        // Store files
        List<FileMetadata> metadata = fileStorageService.storeFiles(files, share, contentKey);
        
        log.info("Created file share: {} with {} files", share.getShareId(), metadata.size());

        return share;
    }

    /**
     * Get share type
     */
    public ShareType getShareType(Share share) {
        return share.getType();
    }
}
