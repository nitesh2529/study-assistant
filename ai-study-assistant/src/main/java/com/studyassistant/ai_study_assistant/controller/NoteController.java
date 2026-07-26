package com.studyassistant.ai_study_assistant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.studyassistant.ai_study_assistant.dto.NoteResponse;
import com.studyassistant.ai_study_assistant.service.NoteService;

/**
 * REST controller exposing the study notes feature: uploading files,
 * listing notes, fetching a single note, and deleting notes.
 * <p>
 * All endpoints require a valid JWT (enforced by {@code SecurityConfig}'s
 * default {@code anyRequest().authenticated()} rule). The authenticated
 * user's id is resolved from the security context, the same way as in
 * {@code ChatController}.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    /**
     * Constructs the controller with the service handling note business logic.
     *
     * @param noteService the service coordinating upload, retrieval, and deletion
     */
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Uploads a study note file (PDF, DOCX, TXT, etc.), extracts its
     * text content, and persists it against the authenticated user.
     *
     * @param file  the uploaded file (multipart form-data)
     * @param title optional title; falls back to the original filename if omitted
     * @return 201 Created with the persisted note
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<NoteResponse> uploadNote(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(value = "title", required = false) String title) {
        String userId = getCurrentUserId();
        NoteResponse response = noteService.uploadNote(userId, file, title);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves the authenticated user's full list of notes, most recent first.
     *
     * @return 200 OK with the list of notes
     */
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes() {
        String userId = getCurrentUserId();
        List<NoteResponse> notes = noteService.getNotes(userId);
        return ResponseEntity.ok(notes);
    }

    /**
     * Retrieves a single note belonging to the authenticated user.
     *
     * @param noteId the id of the note to fetch
     * @return 200 OK with the matching note
     */
    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        NoteResponse note = noteService.getNoteById(userId, noteId);
        return ResponseEntity.ok(note);
    }

    /**
     * Deletes a single note belonging to the authenticated user.
     *
     * @param noteId the id of the note to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable String noteId) {
        String userId = getCurrentUserId();
        noteService.deleteNote(userId, noteId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the authenticated user's id from the Spring Security context.
     * Identical pattern to {@code ChatController.getCurrentUserId()}.
     *
     * @return the authenticated user's id
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
