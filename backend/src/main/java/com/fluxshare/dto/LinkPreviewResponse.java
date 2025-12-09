package com.fluxshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for link preview (WhatsApp-style)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkPreviewResponse {

    private String title;
    
    private String description;
    
    private String type;
    
    private String expiresIn;
    
    private Integer fileCount;
    
    private String thumbnailUrl;
    
    private Boolean requiresPassword;

    /**
     * Create preview for file share
     */
    public static LinkPreviewResponse forFileShare(int fileCount, String expiresIn, boolean requiresPassword) {
        return LinkPreviewResponse.builder()
                .title("FluxShare - Secure File Share")
                .description(fileCount == 1 ? "1 file" : fileCount + " files")
                .type("FILE")
                .expiresIn(expiresIn)
                .fileCount(fileCount)
                .requiresPassword(requiresPassword)
                .build();
    }

    /**
     * Create preview for text share
     */
    public static LinkPreviewResponse forTextShare(String expiresIn, boolean requiresPassword) {
        return LinkPreviewResponse.builder()
                .title("FluxShare - Text Share")
                .description("A shared text snippet")
                .type("TEXT")
                .expiresIn(expiresIn)
                .requiresPassword(requiresPassword)
                .build();
    }

    /**
     * Create preview for code share
     */
    public static LinkPreviewResponse forCodeShare(String language, String expiresIn, boolean requiresPassword) {
        return LinkPreviewResponse.builder()
                .title("FluxShare - Code Share")
                .description("A " + (language != null ? language : "code") + " snippet")
                .type("CODE")
                .expiresIn(expiresIn)
                .requiresPassword(requiresPassword)
                .build();
    }
}
