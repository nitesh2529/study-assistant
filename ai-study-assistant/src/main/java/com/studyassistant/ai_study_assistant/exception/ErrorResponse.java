package com.studyassistant.ai_study_assistant.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard structured error response body returned by
 * {@link GlobalExceptionHandler} for every failed request.
 * <p>
 * Gives API clients a single, predictable JSON shape to parse
 * regardless of which exception triggered the failure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * The HTTP status code, e.g. 404, 409, 401, 500.
     */
    private int status;

    /**
     * The HTTP status reason phrase, e.g. "Not Found", "Conflict".
     */
    private String error;

    /**
     * Human-readable description of what went wrong.
     */
    private String message;

    /**
     * The request path that triggered the error, e.g. "/api/auth/login".
     */
    private String path;

    /**
     * When the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * Per-field validation error messages, keyed by field name.
     * Only populated for validation failures (400 Bad Request from
     * MethodArgumentNotValidException); null/omitted for all other
     * error types thanks to {@code @JsonInclude(NON_NULL)}.
     */
    private Map<String, String> fieldErrors;

}