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
 * MongoDB document representing an AI-generated quiz derived from a
 * study note.
 * <p>
 * Stored in the "quizzes" collection. Each quiz is linked to its
 * source {@link Note} via {@link #noteId}, and contains an embedded
 * list of {@link QuizQuestion} sub-documents - embedded rather than
 * a separate collection, since questions have no independent existence
 * outside their parent quiz.
 */
@Document(collection = "quizzes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    /**
     * MongoDB-generated unique identifier for this quiz.
     */
    @Id
    private String id;

    /**
     * References the {@code id} of the User who owns the source note.
     * Denormalized here (same rationale as Summary.userId) so ownership
     * checks don't require a lookup against the notes collection.
     */
    @Indexed
    private String userId;

    /**
     * References the {@code id} of the Note this quiz was generated from.
     */
    @Indexed
    private String noteId;

    /**
     * Embedded list of questions belonging to this quiz.
     */
    private List<QuizQuestion> questions;

    /**
     * Timestamp of when this quiz was generated.
     * Automatically populated by Spring Data auditing.
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * A single multiple-choice question within a quiz.
     * <p>
     * Embedded directly inside {@link Quiz} - not its own MongoDB
     * document/collection, since a question has no meaning or lifecycle
     * independent of its parent quiz.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizQuestion {

        /**
         * The question text.
         */
        private String questionText;

        /**
         * The list of answer options (e.g. 4 choices for multiple choice).
         */
        private List<String> options;

        /**
         * The correct answer - must exactly match one of the entries in
         * {@link #options}.
         */
        private String correctAnswer;

    }

}
