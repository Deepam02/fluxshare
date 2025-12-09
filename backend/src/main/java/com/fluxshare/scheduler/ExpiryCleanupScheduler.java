package com.fluxshare.scheduler;

import com.fluxshare.entity.Share;
import com.fluxshare.enums.ShareType;
import com.fluxshare.repository.ShareRepository;
import com.fluxshare.service.FileStorageService;
import com.fluxshare.service.TextContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled tasks for cleaning up expired shares
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiryCleanupScheduler {

    private final ShareRepository shareRepository;
    private final FileStorageService fileStorageService;
    private final TextContentService textContentService;

    @Value("${fluxshare.cleanup.enabled:true}")
    private boolean cleanupEnabled;

    /**
     * Clean up expired shares every 15 minutes
     */
    @Scheduled(cron = "${fluxshare.cleanup.cron:0 */15 * * * *}")
    @Transactional
    public void cleanupExpiredShares() {
        if (!cleanupEnabled) {
            log.debug("Cleanup is disabled, skipping");
            return;
        }

        log.info("Starting expired shares cleanup");

        try {
            LocalDateTime now = LocalDateTime.now();
            List<Share> expiredShares = shareRepository.findExpiredShares(now);

            if (expiredShares.isEmpty()) {
                log.info("No expired shares found");
                return;
            }

            log.info("Found {} expired shares to clean up", expiredShares.size());

            int fileSharesDeleted = 0;
            int textSharesDeleted = 0;
            int codeSharesDeleted = 0;

            for (Share share : expiredShares) {
                try {
                    cleanupShare(share);
                    
                    switch (share.getType()) {
                        case FILE -> fileSharesDeleted++;
                        case TEXT -> textSharesDeleted++;
                        case CODE -> codeSharesDeleted++;
                    }
                } catch (Exception e) {
                    log.error("Failed to cleanup share: {}", share.getShareId(), e);
                }
            }

            log.info("Cleanup completed: {} file shares, {} text shares, {} code shares deleted",
                    fileSharesDeleted, textSharesDeleted, codeSharesDeleted);

        } catch (Exception e) {
            log.error("Error during cleanup process", e);
        }
    }

    /**
     * Clean up old expired shares (already marked as expired)
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupOldExpiredShares() {
        if (!cleanupEnabled) {
            return;
        }

        log.info("Starting cleanup of old expired shares");

        try {
            // Delete shares that expired more than 7 days ago
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            shareRepository.deleteExpiredSharesOlderThan(sevenDaysAgo);
            
            log.info("Cleaned up expired shares older than 7 days");
        } catch (Exception e) {
            log.error("Error during old shares cleanup", e);
        }
    }

    /**
     * Clean up a single share
     */
    private void cleanupShare(Share share) {
        log.debug("Cleaning up share: {} of type {}", share.getShareId(), share.getType());

        // Delete associated content
        if (share.getType() == ShareType.FILE) {
            fileStorageService.deleteFilesForShare(share);
        } else if (share.getType() == ShareType.TEXT || share.getType() == ShareType.CODE) {
            textContentService.deleteTextContent(share);
        }

        // Mark as expired
        share.setHasExpired(true);
        shareRepository.save(share);

        log.debug("Share cleaned up: {}", share.getShareId());
    }

    /**
     * Get statistics about shares
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *")
    public void logShareStatistics() {
        try {
            LocalDateTime now = LocalDateTime.now();
            long activeShares = shareRepository.countActiveShares(now);
            
            LocalDateTime oneHourFromNow = now.plusHours(1);
            List<Share> expiringSoon = shareRepository.findSharesExpiringBetween(now, oneHourFromNow);

            log.info("Share Statistics - Active: {}, Expiring in 1 hour: {}", 
                    activeShares, expiringSoon.size());
        } catch (Exception e) {
            log.error("Error logging statistics", e);
        }
    }
}
