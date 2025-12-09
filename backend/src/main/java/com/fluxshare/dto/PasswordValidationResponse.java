package com.fluxshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for password validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValidationResponse {

    private Boolean valid;
    
    private String message;

    public static PasswordValidationResponse success() {
        return PasswordValidationResponse.builder()
                .valid(true)
                .message("Password is valid")
                .build();
    }

    public static PasswordValidationResponse failure() {
        return PasswordValidationResponse.builder()
                .valid(false)
                .message("Invalid password")
                .build();
    }
}
