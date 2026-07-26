package com.studyassistant.ai_study_assistant.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.studyassistant.ai_study_assistant.model.Quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after a note quiz has been generated or retrieved.
 * <p>
 * Returned from {@code POST /api/quizzes/{noteId}/generate},
 * {@code GET /api/quizzes/{noteId}}, and
 * {@code GET /api/quizzes/{noteId}/history}, mirroring the persisted
 * {@link Quiz}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    /**
     * The persisted quiz's unique id.
     */
    private String id;

    /**
     * The id of the source note this quiz was generated from.
     */
    private String noteId;

    /**
     * The quiz's list of questions, each with options and a correct answer.
     */
    private List<Quiz.QuizQuestion> questions;

    /**
     * When this quiz was generated.
     */
    private LocalDateTime createdAt;

}