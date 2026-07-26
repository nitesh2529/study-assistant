package com.studyassistant.ai_study_assistant.controller;

import com.studyassistant.ai_study_assistant.dto.AuthResponse;
import com.studyassistant.ai_study_assistant.dto.LoginRequest;
import com.studyassistant.ai_study_assistant.dto.RegisterRequest;
import com.studyassistant.ai_study_assistant.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing public authentication endpoints.
 * <p>
 * Both endpoints are permitted without a JWT (see {@code SecurityConfig},
 * which whitelists {@code /api/auth/**}), and both return a signed JWT
 * on success for use in subsequent requests.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs the controller with the service handling auth business logic.
     *
     * @param authService the service coordinating registration and login
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     * <p>
     * Validates the request body (see {@link RegisterRequest}), and on
     * success returns a JWT so the client is immediately logged in.
     *
     * @param request the registration payload
     * @return 201 Created with the issued JWT and user details
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates an existing user.
     * <p>
     * Validates the request body (see {@link LoginRequest}), and on
     * success returns a fresh JWT.
     *
     * @param request the login payload
     * @return 200 OK with the issued JWT and user details
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}