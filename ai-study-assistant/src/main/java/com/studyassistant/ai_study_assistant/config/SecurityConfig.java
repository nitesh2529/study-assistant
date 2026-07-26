package com.studyassistant.ai_study_assistant.config;

import com.studyassistant.ai_study_assistant.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central Spring Security configuration for the AI-Assisted Study &amp;
 * Query Assistant.
 * <p>
 * Configures a stateless, JWT-based security model: no HTTP sessions,
 * CSRF disabled (not needed for a token-based API), CORS enabled for
 * the React frontend's origin, public auth endpoints, and all other
 * endpoints requiring a valid JWT processed by {@link JwtAuthenticationFilter}.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructs the security configuration with the JWT filter to be
     * registered into the filter chain.
     *
     * @param jwtAuthenticationFilter the custom filter that authenticates
     *                                requests via a Bearer JWT
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Defines the main security filter chain: disables CSRF, enables CORS,
     * disables sessions, declares public vs. protected endpoints, and
     * registers the JWT filter ahead of Spring's default username/password
     * filter.
     *
     * @param http the HttpSecurity builder
     * @return the configured SecurityFilterChain
     * @throws Exception if the security configuration fails to build
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration allowing the React frontend (running on a
     * different origin during development, e.g. Vite's default
     * localhost:5173) to call this API directly from the browser.
     * <p>
     * Without this, the browser blocks every cross-origin request
     * before it ever reaches a controller - this is a browser-level
     * restriction, independent of the JWT/authorization rules above.
     *
     * @return the CORS configuration source used by the security filter chain
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173","study-assistant-b0gw9ry16-nitesh6.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Password encoder bean used to hash passwords at registration
     * and verify them at login. BCrypt is the industry-standard choice
     * for password storage.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes Spring Security's {@link AuthenticationManager} as a bean
     * so it can be injected into {@code AuthService} to perform
     * credential verification during login.
     *
     * @param config the authentication configuration auto-provided by Spring
     * @return the AuthenticationManager
     * @throws Exception if the manager cannot be retrieved
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
