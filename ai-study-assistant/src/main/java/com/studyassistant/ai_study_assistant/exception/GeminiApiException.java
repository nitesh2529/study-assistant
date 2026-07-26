package com.studyassistant.ai_study_assistant.exception;

/**
 * Thrown when a call to the Google Gemini API fails, times out, or
 * returns a response in an unexpected/malformed structure.
 * <p>
 * Mapped to HTTP 502 (Bad Gateway) by the global exception handler
 * {@code GlobalExceptionHandler} - signaling that the failure
 * originated from an upstream dependency, not this application.
 */
public class GeminiApiException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message only.
     *
     * @param message explanation of what went wrong calling Gemini
     */
    public GeminiApiException(String message) {
        super(message);
    }

    /**
     * Constructs the exception with a descriptive message and the
     * underlying cause (e.g. a WebClient exception, JSON parsing error).
     *
     * @param message explanation of what went wrong calling Gemini
     * @param cause   the original exception that triggered this failure
     */
    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }

}