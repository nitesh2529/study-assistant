package com.studyassistant.ai_study_assistant.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studyassistant.ai_study_assistant.dto.AuthResponse;
import com.studyassistant.ai_study_assistant.dto.LoginRequest;
import com.studyassistant.ai_study_assistant.dto.RegisterRequest;
import com.studyassistant.ai_study_assistant.exception.DuplicateResourceException;
import com.studyassistant.ai_study_assistant.model.User;
import com.studyassistant.ai_study_assistant.repository.UserRepository;
import com.studyassistant.ai_study_assistant.security.JwtUtil;

/**
 * Service layer handling user registration and login business logic.
 * <p>
 * Coordinates {@link UserRepository} for persistence, {@link PasswordEncoder}
 * for hashing, {@link AuthenticationManager} for credential verification,
 * and {@link JwtUtil} for issuing signed tokens.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Constructs the service with all required collaborators via
     * constructor injection.
     *
     * @param userRepository         repository for User persistence
     * @param passwordEncoder        BCrypt encoder for hashing/verifying passwords
     * @param authenticationManager  Spring Security manager for credential checks
     * @param jwtUtil                utility for generating signed JWTs
     */
    public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user: validates uniqueness, hashes the password,
     * persists the account, and issues a JWT immediately so the user
     * is logged in right after signing up.
     *
     * @param request the registration payload (username, email, password)
     * @return an AuthResponse containing the JWT and user details
     * @throws DuplicateResourceException if the username or email is already taken
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("ROLE_USER"))
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getUsername(), savedUser.getRoles());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(savedUser.getRoles())
                .build();
    }

    /**
     * Authenticates an existing user via username/email and password,
     * delegating credential verification to Spring Security's
     * {@link AuthenticationManager}, then issues a fresh JWT.
     *
     * @param request the login payload (usernameOrEmail, password)
     * @return an AuthResponse containing the JWT and user details
     * @throws org.springframework.security.core.AuthenticationException
     *         if credentials are invalid (handled globally as 401)
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found in database - data inconsistency"));

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoles());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

}