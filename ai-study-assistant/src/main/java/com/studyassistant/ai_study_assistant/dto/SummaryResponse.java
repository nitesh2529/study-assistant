package com.studyassistant.ai_study_assistant.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after a note summary has been generated or retrieved.
 * <p>
 * Returned from {@code POST /api/summaries/{noteId}/generate},
 * {@code GET /api/summaries/{noteId}}, and
 * {@code GET /api/summaries/{noteId}/history}, mirroring the persisted
 * {@link com.studyassistant.ai_study_assistant.model.Summary}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {

    /**
     * The persisted summary's unique id.
     */
    private String id;

    /**
     * The id of the source note this summary was generated from.
     */
    private String noteId;

    /**
     * The AI-generated summary text.
     */
    private String summaryText;

    /**
     * When this summary was generated.
     */
    private LocalDateTime createdAt;

}
