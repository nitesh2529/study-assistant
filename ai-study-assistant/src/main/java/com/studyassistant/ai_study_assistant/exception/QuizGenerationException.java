package com.studyassistant.ai_study_assistant.exception;

/**
 * Thrown when the Gemini API's response for a quiz generation request
 * cannot be parsed into valid, well-formed quiz JSON - e.g. malformed
 * JSON, missing fields, or an empty question list.
 * <p>
 * Mapped to HTTP 502 (Bad Gateway) by the global exception handler
 * {@code GlobalExceptionHandler} - signaling that the failure
 * originated from an unreliable upstream AI response, not this
 * application's own logic.
 */
public class QuizGenerationException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message explanation of what went wrong generating the quiz
     */
    public QuizGenerationException(String message) {
        super(message);
    }

}
