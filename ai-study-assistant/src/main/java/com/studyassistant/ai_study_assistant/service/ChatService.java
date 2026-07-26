package com.studyassistant.ai_study_assistant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studyassistant.ai_study_assistant.dto.ChatRequest;
import com.studyassistant.ai_study_assistant.dto.ChatResponse;
import com.studyassistant.ai_study_assistant.exception.ResourceNotFoundException;
import com.studyassistant.ai_study_assistant.model.ChatMessage;
import com.studyassistant.ai_study_assistant.repository.ChatRepository;

/**
 * Service layer handling the AI chat feature: submitting questions,
 * obtaining answers from {@link GeminiService}, persisting the
 * exchange, and retrieving a user's chat history.
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final GeminiService geminiService;

    /**
     * Constructs the service with its required collaborators via
     * constructor injection.
     *
     * @param chatRepository repository for persisting chat exchanges
     * @param geminiService  service that calls the Gemini API for answers
     */
    public ChatService(ChatRepository chatRepository, GeminiService geminiService) {
        this.chatRepository = chatRepository;
        this.geminiService = geminiService;
    }

    /**
     * Handles a full ask-and-save flow: sends the question to Gemini,
     * persists the question/answer pair against the given user, and
     * returns the saved exchange.
     *
     * @param userId  the id of the authenticated user asking the question
     * @param request the question payload
     * @return the persisted chat exchange as a ChatResponse
     */
    public ChatResponse askQuestion(String userId, ChatRequest request) {
        System.out.println("Received question from user " + userId + ": " + request.getQuestion());
        String answer = geminiService.getAnswer(request.getQuestion());

        ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .question(request.getQuestion())
                .answer(answer)
                .build();

        ChatMessage saved = chatRepository.save(chatMessage);

        return toResponse(saved);
    }

    /**
     * Retrieves the full chat history for a user, most recent first.
     *
     * @param userId the id of the authenticated user
     * @return a list of past chat exchanges
     */
    public List<ChatResponse> getHistory(String userId) {
        return chatRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Deletes a single chat message, but only if it belongs to the
     * requesting user - prevents one user from deleting another's history.
     *
     * @param userId    the id of the authenticated user
     * @param messageId the id of the chat message to delete
     * @throws ResourceNotFoundException if no matching message exists for this user
     */
    public void deleteMessage(String userId, String messageId) {
        ChatMessage message = chatRepository.findById(messageId)
                .filter(m -> m.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat message not found: " + messageId));

        chatRepository.deleteById(message.getId());
    }

    /**
     * Maps a persisted ChatMessage document to its API-facing ChatResponse DTO.
     *
     * @param message the persisted chat message
     * @return the corresponding ChatResponse
     */
    private ChatResponse toResponse(ChatMessage message) {
        return ChatResponse.builder()
                .id(message.getId())
                .question(message.getQuestion())
                .answer(message.getAnswer())
                .createdAt(message.getCreatedAt())
                .build();
    }

}
