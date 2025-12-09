package com.fluxshare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for password validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValidationRequest {

    @NotBlank(message = "Password is required")
    private String password;
}
