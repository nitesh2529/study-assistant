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

import com.studyassistant.ai_study_assistant.dto.SummaryResponse;
import com.studyassistant.ai_study_assistant.service.SummaryService;

/**
 * REST controller exposing the note summarization feature: generating
 * a new AI summary, fetching the latest one, and viewing summary
 * history for a given note.
 * <p>
 * All endpoints require a valid JWT. The authenticated user's id is
 * resolved from the security context, the same way as in
 * {@code ChatController} and {@code NoteController}.
 */
@RestController
@RequestMapping("/api/summaries")
public class SummaryController {

    private final SummaryService summaryService;

    /**
     * Constructs the controller with the service handling summary business logic.
     *
     * @param summaryService the service coordinating generation and retrieval
     */
    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    /**
     * Generates a new AI summary for the given note.
     *
     * @param noteId the id of the note to summarize
     * @return 201 Created with the newly generated summary
     */
    @PostMapping("/{noteId}/generate")
    public ResponseEntity<SummaryResponse> generateSummary(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        SummaryResponse response = summaryService.generateSummary(userId, noteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves the most recently generated summary for a note.
     *
     * @param noteId the id of the note whose summary is requested
     * @return 200 OK with the latest summary
     */
    @GetMapping("/{noteId}")
    public ResponseEntity<SummaryResponse> getLatestSummary(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        SummaryResponse response = summaryService.getLatestSummary(userId, noteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the full history of summaries generated for a note.
     *
     * @param noteId the id of the note whose summary history is requested
     * @return 200 OK with the list of summaries, most recent first
     */
    @GetMapping("/{noteId}/history")
    public ResponseEntity<List<SummaryResponse>> getSummaryHistory(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        List<SummaryResponse> history = summaryService.getSummaryHistory(userId, noteId);
        return ResponseEntity.ok(history);
    }

    /**
     * Extracts the authenticated user's id from the Spring Security context.
     * Identical pattern to {@code ChatController} and {@code NoteController}.
     *
     * @return the authenticated user's id
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
