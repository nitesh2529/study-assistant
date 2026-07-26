package com.studyassistant.ai_study_assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting a study question to the AI assistant.
 * <p>
 * Carries the raw input submitted to {@code POST /api/chat/ask}.
 * The authenticated user's id is resolved separately from the
 * security context, not from this payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    /**
     * The question/prompt the user wants answered.
     */
    @NotBlank(message = "Question is required")
    @Size(max = 2000, message = "Question must not exceed 2000 characters")
    private String question;

}
