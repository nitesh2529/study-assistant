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
 * MongoDB document representing an AI-generated summary of a study note.
 * <p>
 * Stored in the "summaries" collection. Each summary is linked to its
 * source {@link Note} via {@link #noteId}, and to the owning user via
 * {@link #userId} for access control without needing a join back to Note.
 */
@Document(collection = "summaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

    /**
     * MongoDB-generated unique identifier for this summary.
     */
    @Id
    private String id;

    /**
     * References the {@code id} of the User who owns the source note.
     * Duplicated from Note here (denormalized) so ownership checks don't
     * require a second lookup against the notes collection.
     */
    @Indexed
    private String userId;

    /**
     * References the {@code id} of the Note this summary was generated from.
     * Indexed to support "does this note already have a summary" lookups.
     */
    @Indexed
    private String noteId;

    /**
     * The AI-generated condensed summary text.
     */
    private String summaryText;

    /**
     * Timestamp of when this summary was generated.
     * Automatically populated by Spring Data auditing.
     */
    @CreatedDate
    private LocalDateTime createdAt;

}