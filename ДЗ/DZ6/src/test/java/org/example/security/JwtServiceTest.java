package org.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    private final UserDetails user = User.withUsername("user")
            .password("{noop}password")
            .roles("USER")
            .build();

    @Test
    void generatedTokenHasThreeParts() {
        String token = jwtService.generateToken(user);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsernameReturnsSubject() {
        String token = jwtService.generateToken(user);
        assertEquals("user", jwtService.extractUsername(token));
    }

    @Test
    void tokenIsValidForMatchingUser() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void tokenIsInvalidForDifferentUser() {
        String token = jwtService.generateToken(user);
        UserDetails other = User.withUsername("admin").password("{noop}x").roles("ADMIN").build();
        assertFalse(jwtService.isTokenValid(token, other));
    }

    @Test
    void malformedTokenIsRejected() {
        assertThrows(Exception.class, () -> jwtService.extractUsername("not.a.jwt"));
    }
}
