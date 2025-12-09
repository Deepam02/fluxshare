package com.fluxshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for file list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileListResponse {

    private String shareId;
    
    private List<FileEntry> files;
    
    private Integer totalFiles;
    
    private Long totalSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileEntry {
        private String name;
        private Long size;
        private String mimeType;
        private Boolean previewable;
        private String downloadUrl;
    }
}
