package com.fluxshare.controller;

import com.fluxshare.dto.CodeContentResponse;
import com.fluxshare.dto.CodeShareRequest;
import com.fluxshare.dto.ShareResponse;
import com.fluxshare.dto.TextContentResponse;
import com.fluxshare.dto.TextShareRequest;
import com.fluxshare.entity.Share;
import com.fluxshare.entity.TextContent;
import com.fluxshare.enums.AccessAction;
import com.fluxshare.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for text and code share operations
 */
@RestController
@RequestMapping("/api/v1/share")
@RequiredArgsConstructor
@Slf4j
public class TextShareController {

    private final TextContentService textContentService;
    private final ShareService shareService;
    private final AccessLogService accessLogService;
    private final RateLimitService rateLimitService;

    /**
     * Create text share
     */
    @PostMapping("/text")
    public ResponseEntity<ShareResponse> createTextShare(
            @Valid @RequestBody TextShareRequest request,
            HttpServletRequest httpRequest) {

        log.info("Creating text share");

        Share share = textContentService.createTextShare(request);

        String shareUrl = buildShareUrl(httpRequest, share.getShareId());

        ShareResponse response = ShareResponse.forTextShare(
                share.getShareId(),
                shareUrl,
                share.getExpiryTime(),
                share.getViewOnce(),
                share.isPasswordProtected(),
                "TEXT"
        );

        accessLogService.logAccess(share, AccessAction.VIEW, httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get text content
     */
    @GetMapping("/{shareId}/text")
    public ResponseEntity<TextContentResponse> getTextContent(
            @PathVariable String shareId,
            @RequestParam(required = false) String password,
            HttpServletRequest request) {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        Share share = shareService.getShareById(shareId);
        shareService.validatePassword(share, password);

        byte[] contentKey = shareService.getContentKey(share);
        String content = textContentService.getTextContent(share, contentKey);

        TextContentResponse response = TextContentResponse.builder()
                .shareId(shareId)
                .content(content)
                .type("TEXT")
                .build();

        // Increment view count
        shareService.incrementViewCount(share);
        accessLogService.logAccess(share, AccessAction.VIEW, request);

        // Handle view-once
        shareService.handleViewOnce(share);

        log.info("Retrieved text content for share {}", shareId);

        return ResponseEntity.ok(response);
    }

    /**
     * Create code share
     */
    @PostMapping("/code")
    public ResponseEntity<ShareResponse> createCodeShare(
            @Valid @RequestBody CodeShareRequest request,
            HttpServletRequest httpRequest) {

        log.info("Creating code share with language: {}", request.getLanguage());

        Share share = textContentService.createCodeShare(request);

        String shareUrl = buildShareUrl(httpRequest, share.getShareId());

        ShareResponse response = ShareResponse.forTextShare(
                share.getShareId(),
                shareUrl,
                share.getExpiryTime(),
                share.getViewOnce(),
                share.isPasswordProtected(),
                "CODE"
        );

        accessLogService.logAccess(share, AccessAction.VIEW, httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get code content
     */
    @GetMapping("/{shareId}/code")
    public ResponseEntity<CodeContentResponse> getCodeContent(
            @PathVariable String shareId,
            @RequestParam(required = false) String password,
            HttpServletRequest request) {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        Share share = shareService.getShareById(shareId);
        shareService.validatePassword(share, password);

        byte[] contentKey = shareService.getContentKey(share);
        String content = textContentService.getTextContent(share, contentKey);
        TextContent textContent = textContentService.getTextContentEntity(share);

        CodeContentResponse response = CodeContentResponse.builder()
                .shareId(shareId)
                .code(content)
                .language(textContent.getLanguage())
                .type("CODE")
                .build();

        // Increment view count
        shareService.incrementViewCount(share);
        accessLogService.logAccess(share, AccessAction.VIEW, request);

        // Handle view-once
        shareService.handleViewOnce(share);

        log.info("Retrieved code content for share {}", shareId);

        return ResponseEntity.ok(response);
    }

    /**
     * Build complete share URL
     */
    private String buildShareUrl(HttpServletRequest request, String shareId) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        
        String portPart = "";
        if ((scheme.equals("http") && serverPort != 80) || 
            (scheme.equals("https") && serverPort != 443)) {
            portPart = ":" + serverPort;
        }
        
        return scheme + "://" + serverName + portPart + "/share/" + shareId;
    }
}
