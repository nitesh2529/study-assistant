package com.studyassistant.ai_study_assistant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyassistant.ai_study_assistant.dto.QuizResponse;
import com.studyassistant.ai_study_assistant.service.QuizService;

/**
 * REST controller exposing the AI quiz generation feature: generating
 * a new quiz, fetching the latest one, and viewing quiz history for a
 * given note.
 * <p>
 * All endpoints require a valid JWT. The authenticated user's id is
 * resolved from the security context, the same way as in
 * {@code ChatController}, {@code NoteController}, and
 * {@code SummaryController}.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    /**
     * Constructs the controller with the service handling quiz business logic.
     *
     * @param quizService the service coordinating generation and retrieval
     */
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Generates a new AI quiz for the given note.
     *
     * @param noteId the id of the note to generate a quiz from
     * @return 201 Created with the newly generated quiz
     */
    @PostMapping("/{noteId}/generate")
    public ResponseEntity<QuizResponse> generateQuiz(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        QuizResponse response = quizService.generateQuiz(userId, noteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves the most recently generated quiz for a note.
     *
     * @param noteId the id of the note whose quiz is requested
     * @return 200 OK with the latest quiz
     */
    @GetMapping("/{noteId}")
    public ResponseEntity<QuizResponse> getLatestQuiz(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        QuizResponse response = quizService.getLatestQuiz(userId, noteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the full history of quizzes generated for a note.
     *
     * @param noteId the id of the note whose quiz history is requested
     * @return 200 OK with the list of quizzes, most recent first
     */
    @GetMapping("/{noteId}/history")
    public ResponseEntity<List<QuizResponse>> getQuizHistory(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        List<QuizResponse> history = quizService.getQuizHistory(userId, noteId);
        return ResponseEntity.ok(history);
    }

    /**
     * Extracts the authenticated user's id from the Spring Security context.
     * Identical pattern to the other feature controllers.
     *
     * @return the authenticated user's id
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
