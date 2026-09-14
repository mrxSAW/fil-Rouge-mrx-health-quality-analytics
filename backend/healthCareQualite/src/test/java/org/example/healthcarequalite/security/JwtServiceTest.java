package org.example.healthcarequalite.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    // Clé réservée aux tests.
    private static final String TEST_SECRET = "0123456789abcdef".repeat(4);

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, 60_000);
    }

    @Test
    void generatedTokenShouldBeValid() {

        String token = jwtService.generate("admin@example.com");

        assertTrue(jwtService.isValid(token));
    }

    @Test
    void tokenShouldContainCorrectEmail() {

        String token = jwtService.generate("admin@example.com");

        String email = jwtService.extractEmail(token);

        assertEquals("admin@example.com", email);
    }

    @Test
    void malformedTokenShouldBeRejected() {

        assertFalse(jwtService.isValid("invalid-token"));
    }

    @Test
    void emptyTokenShouldBeRejected() {

        assertFalse(jwtService.isValid(""));
    }

    @Test
    void nullTokenShouldBeRejected() {

        assertFalse(jwtService.isValid(null));
    }

    @Test
    void expiredTokenShouldBeRejected() {

        JwtService expiredTokenService =
                new JwtService(TEST_SECRET, -60_000);

        String token =
                expiredTokenService.generate("admin@example.com");

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void tokenSignedWithAnotherKeyShouldBeRejected() {

        String anotherSecret = "fedcba9876543210".repeat(4);

        JwtService anotherJwtService =
                new JwtService(anotherSecret, 60_000);

        String token = anotherJwtService.generate("admin@example.com");

        assertFalse(jwtService.isValid(token));
    }
}