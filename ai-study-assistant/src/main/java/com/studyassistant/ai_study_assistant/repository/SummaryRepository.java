package com.studyassistant.ai_study_assistant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.studyassistant.ai_study_assistant.model.Summary;

/**
 * Spring Data MongoDB repository for {@link Summary} documents.
 * <p>
 * Extends {@link MongoRepository} to inherit standard CRUD operations
 * and declares custom query methods scoped to a user or a source note.
 */
@Repository
public interface SummaryRepository extends MongoRepository<Summary, String> {

    /**
     * Finds all summaries belonging to a specific user,
     * ordered by creation time descending (most recent first).
     *
     * @param userId the id of the user whose summaries are requested
     * @return a list of Summary documents for the given user
     */
    List<Summary> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Finds all summaries generated from a specific note.
     *
     * @param noteId the id of the source note
     * @return a list of Summary documents linked to that note
     */
    List<Summary> findByNoteId(String noteId);

    /**
     * Finds the most recently created summary for a specific note, if any.
     * Useful for "get the latest summary" rather than listing all of them.
     *
     * @param noteId the id of the source note
     * @return an Optional containing the most recent Summary, if one exists
     */
    Optional<Summary> findFirstByNoteIdOrderByCreatedAtDesc(String noteId);

    /**
     * Deletes all summaries generated from a specific note.
     * Useful when a note itself is deleted - its summaries should go with it.
     *
     * @param noteId the id of the source note
     */
    void deleteByNoteId(String noteId);

}