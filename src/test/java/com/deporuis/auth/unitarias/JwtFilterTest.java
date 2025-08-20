package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.JwtService;
import com.deporuis.auth.infraestructura.JwtFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtFilter filter;
    private JwtService jwtService;
    private com.deporuis.auth.aplicacion.CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(com.deporuis.auth.aplicacion.CustomUserDetailsService.class);
        filter = new JwtFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_skips_when_no_authorization_header() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn(null);
        when(req.getMethod()).thenReturn("GET");

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_sets_authentication_when_valid_jwt() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");
        when(req.getMethod()).thenReturn("GET");

        when(jwtService.extractUsername("abc.def.ghi")).thenReturn("u1");
        UserDetails ud = User.withUsername("u1").password("p").roles("ADMINISTRADOR").build();
        when(userDetailsService.loadUserByUsername("u1")).thenReturn(ud);
        when(jwtService.isTokenValid("abc.def.ghi", ud)).thenReturn(true);

        filter.doFilter(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("u1", ud.getUsername());
        verify(chain, times(1)).doFilter(req, res);
    }
}
