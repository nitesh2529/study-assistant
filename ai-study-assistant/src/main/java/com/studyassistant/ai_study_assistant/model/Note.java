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
 * MongoDB document representing a study note uploaded by a user.
 * <p>
 * Stored in the "notes" collection. Holds the extracted text content
 * of an uploaded file (PDF/DOCX/TXT), which downstream features
 * (summary generation, quiz generation) will process via Gemini.
 */
@Document(collection = "notes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    /**
     * MongoDB-generated unique identifier for this note.
     */
    @Id
    private String id;

    /**
     * References the {@code id} of the User who uploaded this note.
     * Indexed to make "fetch all notes for a user" queries efficient.
     */
    @Indexed
    private String userId;

    /**
     * User-facing title for the note, e.g. the original filename
     * or a custom name the user provides at upload time.
     */
    private String title;

    /**
     * The extracted plain-text content of the uploaded file.
     * This is what gets sent to Gemini for summarization/quiz generation.
     */
    private String content;

    /**
     * The original uploaded file's name, preserved for display purposes.
     */
    private String originalFileName;

    /**
     * Timestamp of when this note was uploaded.
     * Automatically populated by Spring Data auditing.
     */
    @CreatedDate
    private LocalDateTime createdAt;

}
