package com.studyassistant.ai_study_assistant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.studyassistant.ai_study_assistant.model.Quiz;

/**
 * Spring Data MongoDB repository for {@link Quiz} documents.
 * <p>
 * Extends {@link MongoRepository} to inherit standard CRUD operations
 * and declares custom query methods scoped to a user or a source note.
 */
@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {

    /**
     * Finds all quizzes belonging to a specific user,
     * ordered by creation time descending (most recent first).
     *
     * @param userId the id of the user whose quizzes are requested
     * @return a list of Quiz documents for the given user
     */
    List<Quiz> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Finds all quizzes generated from a specific note.
     *
     * @param noteId the id of the source note
     * @return a list of Quiz documents linked to that note
     */
    List<Quiz> findByNoteId(String noteId);

    /**
     * Finds the most recently created quiz for a specific note, if any.
     * Useful for "get the latest quiz" rather than listing all of them.
     *
     * @param noteId the id of the source note
     * @return an Optional containing the most recent Quiz, if one exists
     */
    Optional<Quiz> findFirstByNoteIdOrderByCreatedAtDesc(String noteId);

    /**
     * Deletes all quizzes generated from a specific note.
     * Useful when a note itself is deleted - its quizzes should go with it.
     *
     * @param noteId the id of the source note
     */
    void deleteByNoteId(String noteId);

}
