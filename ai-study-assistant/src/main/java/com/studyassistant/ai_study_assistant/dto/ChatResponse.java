package com.studyassistant.ai_study_assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO returned after a study question has been answered.
 * <p>
 * Returned from {@code POST /api/chat/ask} and used within chat
 * history listings, mirroring the persisted
 * {@link com.studyassistant.ai_study_assistant.model.ChatMessage}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * The persisted chat message's unique id.
     */
    private String id;

    /**
     * The original question submitted by the user.
     */
    private String question;

    /**
     * The AI-generated answer from Gemini.
     */
    private String answer;

    /**
     * When this chat exchange was created.
     */
    private LocalDateTime createdAt;

}
