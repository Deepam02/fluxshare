package com.fluxshare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating text shares
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextShareRequest {

    @NotBlank(message = "Text content is required")
    private String text;

    @Min(value = 1, message = "Expiry hours must be at least 1")
    private Integer expiryHours;

    private Boolean viewOnce;

    private String password;

    private String notes;

    private Integer maxViews;
}
