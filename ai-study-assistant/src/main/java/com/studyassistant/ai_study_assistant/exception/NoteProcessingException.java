package com.studyassistant.ai_study_assistant.exception;

/**
 * Thrown when processing an uploaded study note fails - e.g. an empty
 * file, unsupported/corrupted file content, or a text extraction failure.
 * <p>
 * Mapped to HTTP 400 (Bad Request) by the global exception handler
 * {@code GlobalExceptionHandler} - the failure is due to the client's
 * uploaded file itself, not a server-side or upstream issue.
 */
public class NoteProcessingException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message explanation of what went wrong processing the note
     */
    public NoteProcessingException(String message) {
        super(message);
    }

}