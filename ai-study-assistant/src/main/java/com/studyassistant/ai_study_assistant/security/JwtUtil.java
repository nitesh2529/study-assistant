package com.studyassistant.ai_study_assistant.security;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utility component for generating, parsing, and validating JSON Web Tokens.
 * <p>
 * Reads the signing secret and expiration window from
 * {@code application.properties} ({@code jwt.secret}, {@code jwt.expiration-ms}).
 * Uses HMAC-SHA256 (HS256) signing.
 */
@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expirationMs;

    /**
     * Constructs the JwtUtil with values injected from application properties.
     *
     * @param secret       Base64-encoded secret key (must be >= 256 bits for HS256)
     * @param expirationMs token validity duration in milliseconds
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration-ms}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT for an authenticated user.
     *
     * @param userId   the user's unique id, stored as the token subject
     * @param username the user's username, stored as a custom claim
     * @param roles    the user's roles, stored as a custom claim
     * @return a signed, compact JWT string
     */
    public String generateToken(String userId, String username, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts the user id (subject) from a token.
     *
     * @param token the JWT string
     * @return the user id encoded as the token's subject
     */
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the username claim from a token.
     *
     * @param token the JWT string
     * @return the username stored in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    /**
     * Extracts the roles claim from a token.
     *
     * @param token the JWT string
     * @return the list of roles stored in the token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    /**
     * Checks whether a token is structurally valid, correctly signed,
     * and not expired.
     *
     * @param token the JWT string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Checks whether a token's expiration timestamp is in the past.
     *
     * @param token the JWT string
     * @return true if expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    /**
     * Generic claim extractor - parses the token and applies the given
     * resolver function to the resulting Claims object.
     *
     * @param token          the JWT string
     * @param claimsResolver function to extract a specific value from Claims
     * @param <T>            the type of the extracted value
     * @return the extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the token and returns all claims, verifying the signature
     * in the process.
     *
     * @param token the JWT string
     * @return the token's Claims payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
