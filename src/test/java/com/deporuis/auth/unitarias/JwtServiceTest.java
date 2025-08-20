package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY",
                "0123456789abcdef0123456789abcdef0123");
    }

    @Test
    void generate_and_validate_token_ok() {
        UserDetails ud = User.withUsername("u123").password("p").roles("ADMINISTRADOR").build();
        String token = jwtService.generateToken(ud);

        assertNotNull(token);
        assertFalse(token.isBlank());

        String username = jwtService.extractUsername(token);
        assertEquals("u123", username);
        assertTrue(jwtService.isTokenValid(token, ud));
    }

    @Test
    void isTokenValid_false_when_username_differs() {
        UserDetails ud = User.withUsername("alice").password("x").roles("ENTRENADOR").build();
        String token = jwtService.generateToken(ud);

        UserDetails other = User.withUsername("bob").password("x").roles("ENTRENADOR").build();
        assertFalse(jwtService.isTokenValid(token, other));
    }
}
