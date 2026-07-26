package com.studyassistant.ai_study_assistant.exception;

/**
 * Thrown when a requested resource cannot be found -
 * e.g. looking up a user by id, or a chat message that doesn't exist
 * or doesn't belong to the requesting user.
 * <p>
 * Mapped to HTTP 404 (Not Found) by the global exception handler
 * {@code GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message explanation of which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

}