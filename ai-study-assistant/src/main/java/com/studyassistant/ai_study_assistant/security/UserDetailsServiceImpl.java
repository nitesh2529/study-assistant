package com.studyassistant.ai_study_assistant.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.studyassistant.ai_study_assistant.model.User;
import com.studyassistant.ai_study_assistant.repository.UserRepository;

/**
 * Spring Security {@link UserDetailsService} implementation backed by
 * MongoDB via {@link UserRepository}.
 * <p>
 * Resolves a login identifier (username or email) to a
 * {@link UserDetails} instance, which {@link AuthenticationManager}
 * uses internally to verify credentials during authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs the service with the repository used to look up users.
     *
     * @param userRepository the MongoDB repository for User documents
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by username or email, as expected by
     * {@link UserDetailsService}. Tries username first, then falls
     * back to email, since our login flow accepts either.
     *
     * @param usernameOrEmail the identifier submitted at login
     * @return a Spring Security UserDetails wrapping the found User
     * @throws UsernameNotFoundException if no matching user exists
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with username or email: " + usernameOrEmail));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .build();
    }

}