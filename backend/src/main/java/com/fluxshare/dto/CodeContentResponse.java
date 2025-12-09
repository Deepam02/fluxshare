package com.fluxshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for code content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeContentResponse {

    private String shareId;
    
    private String code;
    
    private String language;
    
    private String type;
    
    private String message;
}
