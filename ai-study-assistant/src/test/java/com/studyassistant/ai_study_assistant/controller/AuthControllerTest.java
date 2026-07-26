package com.studyassistant.ai_study_assistant.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyassistant.ai_study_assistant.dto.AuthResponse;
import com.studyassistant.ai_study_assistant.dto.LoginRequest;
import com.studyassistant.ai_study_assistant.dto.RegisterRequest;
import com.studyassistant.ai_study_assistant.exception.DuplicateResourceException;
import com.studyassistant.ai_study_assistant.security.JwtAuthenticationFilter;
import com.studyassistant.ai_study_assistant.security.JwtUtil;
import com.studyassistant.ai_study_assistant.service.AuthService;

/**
 * Slice test for {@link AuthController}, verifying HTTP-layer behavior
 * (routing, status codes, validation, JSON mapping) with
 * {@link AuthService} mocked out.
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

   @MockitoBean
    private AuthService authService;

    // Security beans required to satisfy the application context in this slice,
    // even though authentication logic itself isn't exercised by these tests.
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser
    void register_shouldReturn201_whenRequestIsValid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("nitesh")
                .email("nitesh@example.com")
                .password("plainPassword123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("mocked-jwt-token")
                .userId("user-123")
                .username("nitesh")
                .email("nitesh@example.com")
                .roles(List.of("ROLE_USER"))
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.username").value("nitesh"));
    }

    @Test
    @WithMockUser
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("nitesh")
                .email("not-a-valid-email")
                .password("plainPassword123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    @WithMockUser
    void register_shouldReturn409_whenUsernameAlreadyExists() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("nitesh")
                .email("nitesh@example.com")
                .password("plainPassword123")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("Username is already taken: nitesh"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username is already taken: nitesh"));
    }

    @Test
    @WithMockUser
    void login_shouldReturn200_whenCredentialsAreValid() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("nitesh")
                .password("plainPassword123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("mocked-jwt-token")
                .userId("user-123")
                .username("nitesh")
                .email("nitesh@example.com")
                .roles(List.of("ROLE_USER"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    @WithMockUser
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("nitesh")
                .password("wrongPassword")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username/email or password"));
    }

}