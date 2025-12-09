package com.fluxshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for share metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareMetadataResponse {

    private String shareId;
    
    private String type;
    
    private LocalDateTime expiryTime;
    
    private String timeRemaining;
    
    private Boolean viewOnce;
    
    private Boolean passwordProtected;
    
    private LocalDateTime createdAt;
    
    private String notes;
    
    private List<FileInfo> files;
    
    private Integer viewCount;
    
    private Integer downloadCount;
    
    private Integer maxDownloads;
    
    private Integer maxViews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        private String name;
        private Long size;
        private String mimeType;
        private Boolean previewable;
    }
}
