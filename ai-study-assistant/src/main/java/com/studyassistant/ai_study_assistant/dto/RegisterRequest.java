package com.studyassistant.ai_study_assistant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user registration.
 * <p>
 * Carries and validates the raw input submitted to
 * {@code POST /api/auth/register}, decoupling the API contract
 * from the internal {@link com.studyassistant.ai_study_assistant.model.User} document.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Desired username. Must be unique, enforced at the service layer
     * via {@code UserRepository#existsByUsername}.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    /**
     * Email address. Must be a valid format and unique, enforced at
     * the service layer via {@code UserRepository#existsByEmail}.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    /**
     * Plain-text password as submitted by the client.
     * Hashed with BCrypt in the service layer before persistence -
     * never stored or logged in plain text.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    private String password;

}
