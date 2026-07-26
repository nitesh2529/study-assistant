package com.studyassistant.ai_study_assistant.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after successful authentication
 * (registration or login) at
 * {@code POST /api/auth/register} or {@code POST /api/auth/login}.
 * <p>
 * Carries the issued JWT along with minimal, non-sensitive
 * user details for immediate client-side use.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * The signed JWT access token the client must include as
     * {@code Authorization: Bearer <token>} on subsequent requests.
     */
    private String token;

    /**
     * The token type, always "Bearer" for this API.
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * The authenticated user's unique id.
     */
    private String userId;

    /**
     * The authenticated user's username.
     */
    private String username;

    /**
     * The authenticated user's email.
     */
    private String email;

    /**
     * The roles assigned to the authenticated user,
     * e.g. ["ROLE_USER"].
     */
    private List<String> roles;

}