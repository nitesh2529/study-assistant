package com.studyassistant.ai_study_assistant.service;

import com.studyassistant.ai_study_assistant.dto.AuthResponse;
import com.studyassistant.ai_study_assistant.dto.LoginRequest;
import com.studyassistant.ai_study_assistant.dto.RegisterRequest;
import com.studyassistant.ai_study_assistant.exception.DuplicateResourceException;
import com.studyassistant.ai_study_assistant.model.User;
import com.studyassistant.ai_study_assistant.repository.UserRepository;
import com.studyassistant.ai_study_assistant.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthService}, verifying registration and login
 * business logic in isolation using mocked collaborators.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("nitesh")
                .email("nitesh@example.com")
                .password("plainPassword123")
                .build();

        loginRequest = LoginRequest.builder()
                .usernameOrEmail("nitesh")
                .password("plainPassword123")
                .build();

        savedUser = User.builder()
                .id("user-123")
                .username("nitesh")
                .email("nitesh@example.com")
                .password("hashedPassword")
                .roles(List.of("ROLE_USER"))
                .enabled(true)
                .build();
    }

    @Test
    void register_shouldCreateUserAndReturnToken_whenUsernameAndEmailAreUnique() {
        when(userRepository.existsByUsername("nitesh")).thenReturn(false);
        when(userRepository.existsByEmail("nitesh@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("user-123", "nitesh", List.of("ROLE_USER")))
                .thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("mocked-jwt-token");
        assertThat(response.getUserId()).isEqualTo("user-123");
        assertThat(response.getUsername()).isEqualTo("nitesh");
        assertThat(response.getEmail()).isEqualTo("nitesh@example.com");
        assertThat(response.getRoles()).containsExactly("ROLE_USER");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowDuplicateResourceException_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("nitesh")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username is already taken");

        verify(userRepository, org.mockito.Mockito.never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowDuplicateResourceException_whenEmailAlreadyExists() {
        when(userRepository.existsByUsername("nitesh")).thenReturn(false);
        when(userRepository.existsByEmail("nitesh@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email is already registered");

        verify(userRepository, org.mockito.Mockito.never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        when(userRepository.findByUsername("nitesh")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken("user-123", "nitesh", List.of("ROLE_USER")))
                .thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("mocked-jwt-token");
        assertThat(response.getUserId()).isEqualTo("user-123");
        assertThat(response.getUsername()).isEqualTo("nitesh");

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_shouldThrowIllegalStateException_whenAuthenticatedUserMissingFromDatabase() {
        when(userRepository.findByUsername("nitesh")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nitesh")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Authenticated user not found");
    }

}