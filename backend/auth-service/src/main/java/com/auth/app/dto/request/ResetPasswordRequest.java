package com.auth.app.dto.request;

import com.auth.app.validation.PasswordMatches;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank
    @Size(min = 12, message = "Password must be at least 12 characters long")
    private String password;

    @NotBlank(message = "You must confirm the password")
    private String confirmPassword;
}
