package com.studyassistant.ai_study_assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO returned after a note has been uploaded or retrieved.
 * <p>
 * Returned from {@code POST /api/notes/upload}, {@code GET /api/notes},
 * and {@code GET /api/notes/{id}}, mirroring the persisted
 * {@link com.studyassistant.ai_study_assistant.model.Note}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {

    /**
     * The persisted note's unique id.
     */
    private String id;

    /**
     * The note's title (user-provided, or falls back to the filename).
     */
    private String title;

    /**
     * The extracted plain-text content of the note.
     */
    private String content;

    /**
     * The original uploaded file's name.
     */
    private String originalFileName;

    /**
     * When this note was uploaded.
     */
    private LocalDateTime createdAt;

}
