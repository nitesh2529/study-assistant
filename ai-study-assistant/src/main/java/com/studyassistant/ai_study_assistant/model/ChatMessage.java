package com.studyassistant.ai_study_assistant.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document representing a single question-answer exchange
 * between a user and the Gemini-powered study assistant.
 * <p>
 * Stored in the "chat_messages" collection. Each record is linked
 * to the owning user via {@link #userId}.
 */
@Document(collection = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /**
     * MongoDB-generated unique identifier for this chat message.
     */
    @Id
    private String id;

    /**
     * References the {@code id} of the User who asked the question.
     * Indexed to make "fetch all chats for a user" queries efficient.
     */
    @Indexed
    private String userId;

    /**
     * The question/prompt submitted by the user.
     */
    private String question;

    /**
     * The AI-generated answer returned by the Gemini API.
     */
    private String answer;

    /**
     * Timestamp of when this chat exchange occurred.
     * Automatically populated by Spring Data auditing.
     */
    @CreatedDate
    private LocalDateTime createdAt;

}