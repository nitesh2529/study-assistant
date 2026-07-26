package com.studyassistant.ai_study_assistant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user login.
 * <p>
 * Carries the raw credentials submitted to
 * {@code POST /api/auth/login}. The {@code usernameOrEmail} field
 * allows the client to authenticate with either identifier.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * The username or email address used to identify the account.
     * The service layer will attempt to resolve this against both
     * {@code UserRepository#findByUsername} and
     * {@code UserRepository#findByEmail}.
     */
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    /**
     * Plain-text password as submitted by the client.
     * Verified against the stored BCrypt hash in the service layer -
     * never logged in plain text.
     */
    @NotBlank(message = "Password is required")
    private String password;

}
