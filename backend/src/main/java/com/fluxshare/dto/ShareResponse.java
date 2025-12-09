package com.fluxshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for share creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponse {

    private String shareId;
    
    private String shareUrl;
    
    private LocalDateTime expiryTime;
    
    private Boolean viewOnce;
    
    private Boolean passwordProtected;
    
    private Integer fileCount;
    
    private String type;
    
    private String message;

    /**
     * Create a simple response with just share ID
     */
    public static ShareResponse simple(String shareId) {
        return ShareResponse.builder()
                .shareId(shareId)
                .build();
    }

    /**
     * Create a response for file share
     */
    public static ShareResponse forFileShare(String shareId, String shareUrl,
                                             LocalDateTime expiryTime, 
                                             Boolean viewOnce, Boolean passwordProtected, 
                                             Integer fileCount) {
        return ShareResponse.builder()
                .shareId(shareId)
                .shareUrl(shareUrl)
                .expiryTime(expiryTime)
                .viewOnce(viewOnce)
                .passwordProtected(passwordProtected)
                .fileCount(fileCount)
                .type("FILE")
                .build();
    }

    /**
     * Create a response for text/code share
     */
    public static ShareResponse forTextShare(String shareId, String shareUrl,
                                            LocalDateTime expiryTime, 
                                            Boolean viewOnce, Boolean passwordProtected, 
                                            String type) {
        return ShareResponse.builder()
                .shareId(shareId)
                .shareUrl(shareUrl)
                .expiryTime(expiryTime)
                .viewOnce(viewOnce)
                .passwordProtected(passwordProtected)
                .type(type)
                .build();
    }
}
