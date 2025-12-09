package com.fluxshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for text content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextContentResponse {

    private String shareId;
    
    private String content;
    
    private String type;
    
    private String message;
}
