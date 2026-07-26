package com.studyassistant.ai_study_assistant.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.studyassistant.ai_study_assistant.model.Note;

/**
 * Spring Data MongoDB repository for {@link Note} documents.
 * <p>
 * Extends {@link MongoRepository} to inherit standard CRUD operations
 * and declares custom query methods scoped to a specific user's notes.
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Finds all notes belonging to a specific user,
     * ordered by creation time descending (most recent first).
     *
     * @param userId the id of the user whose notes are requested
     * @return a list of Note documents for the given user
     */
    List<Note> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Deletes all notes belonging to a specific user.
     * Useful for account deletion.
     *
     * @param userId the id of the user whose notes should be deleted
     */
    void deleteByUserId(String userId);

    /**
     * Counts how many notes a specific user has uploaded.
     * Can be used for usage limits (e.g. free-tier upload caps) later.
     *
     * @param userId the id of the user
     * @return the total count of notes for that user
     */
    long countByUserId(String userId);

}
