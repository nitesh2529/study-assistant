package com.studyassistant.ai_study_assistant.repository;

import com.studyassistant.ai_study_assistant.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for {@link ChatMessage} documents.
 * <p>
 * Extends {@link MongoRepository} to inherit standard CRUD operations
 * and declares custom query methods for retrieving a user's chat history.
 */
@Repository
public interface ChatRepository extends MongoRepository<ChatMessage, String> {

    /**
     * Finds all chat messages belonging to a specific user,
     * ordered by creation time descending (most recent first).
     *
     * @param userId the id of the user whose chat history is requested
     * @return a list of ChatMessage documents for the given user
     */
    List<ChatMessage> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Deletes all chat messages belonging to a specific user.
     * Useful for account deletion or "clear history" functionality.
     *
     * @param userId the id of the user whose chat history should be deleted
     */
    void deleteByUserId(String userId);

    /**
     * Counts how many chat messages a specific user has created.
     * Can be used for usage limits or analytics later.
     *
     * @param userId the id of the user
     * @return the total count of chat messages for that user
     */
    long countByUserId(String userId);

}
