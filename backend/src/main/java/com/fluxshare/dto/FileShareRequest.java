package com.fluxshare.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating file shares
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShareRequest {

    @Min(value = 1, message = "Expiry hours must be at least 1")
    private Integer expiryHours;

    private Boolean viewOnce;

    private String password;

    private String notes;

    private Integer maxDownloads;

    private Integer maxViews;

    @Builder.Default
    private Boolean enablePreview = true;
}
