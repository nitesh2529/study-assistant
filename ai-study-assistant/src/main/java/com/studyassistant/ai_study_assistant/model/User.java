package com.studyassistant.ai_study_assistant.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document representing a registered user of the
 * AI-Assisted Study & Query Assistant.
 * <p>
 * Stored in the "users" collection. Passwords are always persisted
 * as BCrypt hashes - never in plain text.
 */
@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * MongoDB-generated unique identifier for the user.
     */
    @Id
    private String id;

    /**
     * Unique username used for login/display purposes.
     */
    @Indexed(unique = true)
    private String username;

    /**
     * Unique email address, also usable for login.
     */
    @Indexed(unique = true)
    private String email;

    /**
     * BCrypt-hashed password. Never store or return plain text passwords.
     */
    private String password;

    /**
     * Roles assigned to the user, e.g. "ROLE_USER", "ROLE_ADMIN".
     * Defaults to a single "ROLE_USER" role at registration time.
     */
    @Builder.Default
    private List<String> roles = List.of("ROLE_USER");

    /**
     * Timestamp of when the user account was created.
     * Automatically populated by Spring Data auditing.
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Whether the user account is enabled and allowed to log in.
     * Defaults to true; can be used later for account deactivation.
     */
    @Builder.Default
    private boolean enabled = true;

}