package com.studyassistant.ai_study_assistant.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.studyassistant.ai_study_assistant.model.User;

/**
 * Spring Data MongoDB repository for {@link User} documents.
 * <p>
 * Extends {@link MongoRepository} to inherit standard CRUD operations
 * (save, findById, findAll, delete, etc.) and declares custom query
 * methods derived from method naming conventions.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their unique username.
     * Used during login to authenticate against a username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email address.
     * Used during login to authenticate against an email.
     *
     * @param email the email to search for
     * @return an Optional containing the User if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given username already exists.
     * Used during registration to prevent duplicate usernames.
     *
     * @param username the username to check
     * @return true if a user with this username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether a user with the given email already exists.
     * Used during registration to prevent duplicate emails.
     *
     * @param email the email to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

}
