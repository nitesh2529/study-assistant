package com.studyassistant.ai_study_assistant.controller;

import com.studyassistant.ai_study_assistant.dto.ChatRequest;
import com.studyassistant.ai_study_assistant.dto.ChatResponse;
import com.studyassistant.ai_study_assistant.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing the AI chat feature: asking questions,
 * retrieving chat history, and deleting past messages.
 * <p>
 * All endpoints require a valid JWT (enforced by
 * {@code SecurityConfig}'s default {@code anyRequest().authenticated()}
 * rule). The authenticated user's id is resolved from the security
 * context, populated by {@code JwtAuthenticationFilter}.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * Constructs the controller with the service handling chat business logic.
     *
     * @param chatService the service coordinating question-answering and history
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Submits a question to the AI assistant and returns the answer.
     *
     * @param request the question payload
     * @return 201 Created with the persisted question/answer exchange
     */
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(@Valid @RequestBody ChatRequest request) {
        String userId = getCurrentUserId();
        ChatResponse response = chatService.askQuestion(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves the authenticated user's full chat history, most recent first.
     *
     * @return 200 OK with the list of past chat exchanges
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatResponse>> getHistory() {
        String userId = getCurrentUserId();
        List<ChatResponse> history = chatService.getHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Deletes a single chat message belonging to the authenticated user.
     *
     * @param messageId the id of the chat message to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String messageId) {
        String userId = getCurrentUserId();
        chatService.deleteMessage(userId, messageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the authenticated user's id from the Spring Security context.
     * <p>
     * Relies on {@code JwtAuthenticationFilter} having set the JWT
     * subject (the user's id) as the authentication principal's name.
     *
     * @return the authenticated user's id
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
