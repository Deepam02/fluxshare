package com.fluxshare.controller;

import com.fluxshare.dto.FileListResponse;
import com.fluxshare.dto.FileShareRequest;
import com.fluxshare.dto.ShareResponse;
import com.fluxshare.entity.FileMetadata;
import com.fluxshare.entity.Share;
import com.fluxshare.enums.AccessAction;
import com.fluxshare.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for file share operations
 */
@RestController
@RequestMapping("/api/v1/share")
@RequiredArgsConstructor
@Slf4j
public class FileShareController {

    private final ShareFactoryService shareFactoryService;
    private final ShareService shareService;
    private final FileStorageService fileStorageService;
    private final AccessLogService accessLogService;
    private final RateLimitService rateLimitService;

    /**
     * Upload files and create share
     */
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShareResponse> createFileShare(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) Integer expiryHours,
            @RequestParam(required = false) Boolean viewOnce,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) Integer maxDownloads,
            @RequestParam(required = false) Integer maxViews,
            HttpServletRequest request) {

        log.info("Creating file share with {} files", files.size());

        FileShareRequest shareRequest = FileShareRequest.builder()
                .expiryHours(expiryHours)
                .viewOnce(viewOnce)
                .password(password)
                .notes(notes)
                .maxDownloads(maxDownloads)
                .maxViews(maxViews)
                .build();

        Share share = shareFactoryService.createFileShare(files, shareRequest);

        // Build share URL from request context
        String shareUrl = buildShareUrl(request, share.getShareId());

        ShareResponse response = ShareResponse.forFileShare(
                share.getShareId(),
                shareUrl,
                share.getExpiryTime(),
                share.getViewOnce(),
                share.isPasswordProtected(),
                files.size()
        );

        accessLogService.logAccess(share, AccessAction.VIEW, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    /**
     * Get list of files in a share
     */
    @GetMapping("/{shareId}/files")
    public ResponseEntity<FileListResponse> getFileList(
            @PathVariable String shareId,
            @RequestParam(required = false) String password,
            HttpServletRequest request) {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        Share share = shareService.getShareById(shareId);
        shareService.validatePassword(share, password);

        List<FileMetadata> files = fileStorageService.getFilesForShare(share);
        
        List<FileListResponse.FileEntry> fileEntries = files.stream()
                .map(f -> FileListResponse.FileEntry.builder()
                        .name(f.getFilename())
                        .size(f.getSize())
                        .mimeType(f.getMimeType())
                        .previewable(f.canPreview())
                        .downloadUrl("/api/v1/share/" + shareId + "/files/" + f.getFilename())
                        .build())
                .collect(Collectors.toList());

        Long totalSize = files.stream().mapToLong(FileMetadata::getSize).sum();

        FileListResponse response = FileListResponse.builder()
                .shareId(shareId)
                .files(fileEntries)
                .totalFiles(files.size())
                .totalSize(totalSize)
                .build();

        accessLogService.logAccess(share, AccessAction.METADATA_ACCESS, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Download a single file
     */
    @GetMapping("/{shareId}/files/{fileName}")
    public void downloadFile(
            @PathVariable String shareId,
            @PathVariable String fileName,
            @RequestParam(required = false) String password,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        Share share = shareService.getShareById(shareId);
        shareService.validatePassword(share, password);

        FileMetadata fileMetadata = fileStorageService.getFileByName(share, fileName);
        byte[] contentKey = shareService.getContentKey(share);

        // Set response headers
        response.setContentType(fileMetadata.getMimeType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + fileMetadata.getFilename() + "\"");
        response.setContentLengthLong(fileMetadata.getSize());

        // Stream file
        try (OutputStream outputStream = response.getOutputStream()) {
            fileStorageService.streamFile(fileMetadata, outputStream, contentKey);
        }

        // Increment download count
        shareService.incrementDownloadCount(share);
        accessLogService.logAccess(share, AccessAction.DOWNLOAD, request, fileName, true, null);

        // Handle view-once
        shareService.handleViewOnce(share);

        log.info("Downloaded file: {} from share {}", fileName, shareId);
    }

    /**
     * Download all files as ZIP
     */
    @GetMapping("/{shareId}/download/all")
    public void downloadAllAsZip(
            @PathVariable String shareId,
            @RequestParam(required = false) String password,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        Share share = shareService.getShareById(shareId);
        shareService.validatePassword(share, password);

        byte[] contentKey = shareService.getContentKey(share);

        // Set response headers
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + shareId + ".zip\"");

        // Stream ZIP
        try (OutputStream outputStream = response.getOutputStream()) {
            fileStorageService.createZipForShare(share, outputStream, contentKey);
        }

        // Increment download count
        shareService.incrementDownloadCount(share);
        accessLogService.logAccess(share, AccessAction.ZIP_DOWNLOAD, request);

        // Handle view-once
        shareService.handleViewOnce(share);

        log.info("Downloaded all files as ZIP from share {}", shareId);
    }

    /**
     * Preview a file (limited bytes)
     */
    @GetMapping("/{shareId}/files/{fileName}/preview")
    public ResponseEntity<byte[]> previewFile(
            @PathVariable String shareId,
            @PathVariable String fileName,
            @RequestParam(required = false) String password,
            @RequestParam(defaultValue = "102400") int maxBytes,
            HttpServletRequest request) {

        // Rate limiting
        rateLimitService.checkRateLimit(shareId, request.getRemoteAddr());

        Share share = shareService.getShareById(shareId);
        shareService.validatePassword(share, password);

        FileMetadata fileMetadata = fileStorageService.getFileByName(share, fileName);
        byte[] contentKey = shareService.getContentKey(share);

        byte[] preview = fileStorageService.getFilePreview(fileMetadata, contentKey, maxBytes);

        accessLogService.logAccess(share, AccessAction.PREVIEW, request, fileName, true, null);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileMetadata.getMimeType()))
                .body(preview);
    }
}
