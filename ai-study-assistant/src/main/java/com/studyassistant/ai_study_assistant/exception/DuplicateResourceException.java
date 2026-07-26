package com.studyassistant.ai_study_assistant.exception;

/**
 * Thrown when an operation would create a duplicate resource -
 * e.g. registering with a username or email that already exists.
 * <p>
 * Mapped to HTTP 409 (Conflict) by the global exception handler
 * {@code GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message explanation of which resource was duplicated
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

}
