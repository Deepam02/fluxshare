package com.fluxshare.controller;

import com.fluxshare.dto.LinkPreviewResponse;
import com.fluxshare.dto.PasswordValidationRequest;
import com.fluxshare.dto.PasswordValidationResponse;
import com.fluxshare.dto.ShareMetadataResponse;
import com.fluxshare.entity.Share;
import com.fluxshare.enums.AccessAction;
import com.fluxshare.exception.InvalidPasswordException;
import com.fluxshare.service.AccessLogService;
import com.fluxshare.service.RateLimitService;
import com.fluxshare.service.ShareService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for share access and metadata operations
 */
@RestController
@RequestMapping("/api/v1/share")
@RequiredArgsConstructor
@Slf4j
public class ShareAccessController {

    private final ShareService shareService;
    private final AccessLogService accessLogService;
    private final RateLimitService rateLimitService;

    /**
     * Get share metadata
     */
    @GetMapping("/{shareId}/metadata")
    public ResponseEntity<ShareMetadataResponse> getMetadata(
            @PathVariable String shareId,
            HttpServletRequest request) {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        ShareMetadataResponse response = shareService.getMetadata(shareId);

        // Log access
        Share share = shareService.getShareByIdWithoutValidation(shareId);
        accessLogService.logAccess(share, AccessAction.METADATA_ACCESS, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Validate password for a share
     */
    @PostMapping("/{shareId}/validate")
    public ResponseEntity<PasswordValidationResponse> validatePassword(
            @PathVariable String shareId,
            @Valid @RequestBody PasswordValidationRequest request,
            HttpServletRequest httpRequest) {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, httpRequest.getRemoteAddr());

        Share share = shareService.getShareByIdWithoutValidation(shareId);
        
        try {
            shareService.validatePassword(share, request.getPassword());
            
            accessLogService.logAccess(share, AccessAction.VALIDATE_PASSWORD, 
                    httpRequest, null, true, null);
            
            return ResponseEntity.ok(PasswordValidationResponse.success());
        } catch (InvalidPasswordException e) {
            accessLogService.logAccess(share, AccessAction.VALIDATE_PASSWORD, 
                    httpRequest, null, false, "Invalid password");
            
            return ResponseEntity.ok(PasswordValidationResponse.failure());
        }
    }

    /**
     * Delete a share
     */
    @DeleteMapping("/{shareId}")
    public ResponseEntity<Void> deleteShare(
            @PathVariable String shareId,
            @RequestParam(required = false) String password,
            HttpServletRequest request) {

        Share share = shareService.getShareByIdWithoutValidation(shareId);
        shareService.validatePassword(share, password);

        shareService.deleteShare(shareId);

        log.info("Share deleted: {}", shareId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get link preview (WhatsApp-style)
     */
    @GetMapping("/{shareId}/preview")
    public ResponseEntity<LinkPreviewResponse> getLinkPreview(
            @PathVariable String shareId,
            HttpServletRequest request) {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        LinkPreviewResponse response = shareService.getLinkPreview(shareId);

        // Log access
        Share share = shareService.getShareByIdWithoutValidation(shareId);
        accessLogService.logAccess(share, AccessAction.PREVIEW, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
