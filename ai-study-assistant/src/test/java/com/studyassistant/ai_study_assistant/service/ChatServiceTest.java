package com.studyassistant.ai_study_assistant.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.studyassistant.ai_study_assistant.dto.ChatRequest;
import com.studyassistant.ai_study_assistant.dto.ChatResponse;
import com.studyassistant.ai_study_assistant.exception.ResourceNotFoundException;
import com.studyassistant.ai_study_assistant.model.ChatMessage;
import com.studyassistant.ai_study_assistant.repository.ChatRepository;

/**
 * Unit tests for {@link ChatService}, verifying the ask-and-save flow,
 * chat history retrieval, and ownership-scoped deletion in isolation
 * using mocked collaborators.
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private ChatService chatService;

    private static final String USER_ID = "user-123";
    private static final String OTHER_USER_ID = "user-999";

    private ChatMessage savedMessage;

    @BeforeEach
    void setUp() {
        savedMessage = ChatMessage.builder()
                .id("msg-001")
                .userId(USER_ID)
                .question("What is polymorphism?")
                .answer("Polymorphism is...")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void askQuestion_shouldCallGeminiAndPersistExchange() {
        ChatRequest request = ChatRequest.builder()
                .question("What is polymorphism?")
                .build();

        when(geminiService.getAnswer("What is polymorphism?"))
                .thenReturn("Polymorphism is...");
        when(chatRepository.save(any(ChatMessage.class))).thenReturn(savedMessage);

        ChatResponse response = chatService.askQuestion(USER_ID, request);

        assertThat(response.getId()).isEqualTo("msg-001");
        assertThat(response.getQuestion()).isEqualTo("What is polymorphism?");
        assertThat(response.getAnswer()).isEqualTo("Polymorphism is...");

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(captor.getValue().getAnswer()).isEqualTo("Polymorphism is...");
    }

    @Test
    void getHistory_shouldReturnMappedResponsesForUser() {
        when(chatRepository.findByUserIdOrderByCreatedAtDesc(USER_ID))
                .thenReturn(List.of(savedMessage));

        List<ChatResponse> history = chatService.getHistory(USER_ID);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getId()).isEqualTo("msg-001");
        assertThat(history.get(0).getQuestion()).isEqualTo("What is polymorphism?");
    }

    @Test
    void getHistory_shouldReturnEmptyList_whenUserHasNoMessages() {
        when(chatRepository.findByUserIdOrderByCreatedAtDesc(USER_ID))
                .thenReturn(List.of());

        List<ChatResponse> history = chatService.getHistory(USER_ID);

        assertThat(history).isEmpty();
    }

    @Test
    void deleteMessage_shouldDelete_whenMessageBelongsToUser() {
        when(chatRepository.findById("msg-001")).thenReturn(Optional.of(savedMessage));

        chatService.deleteMessage(USER_ID, "msg-001");

        verify(chatRepository).deleteById("msg-001");
    }

    @Test
    void deleteMessage_shouldThrowResourceNotFound_whenMessageBelongsToDifferentUser() {
        when(chatRepository.findById("msg-001")).thenReturn(Optional.of(savedMessage));

        assertThatThrownBy(() -> chatService.deleteMessage(OTHER_USER_ID, "msg-001"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat message not found");

        verify(chatRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteMessage_shouldThrowResourceNotFound_whenMessageDoesNotExist() {
        when(chatRepository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.deleteMessage(USER_ID, "missing-id"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(chatRepository, never()).deleteById(anyString());
    }

}