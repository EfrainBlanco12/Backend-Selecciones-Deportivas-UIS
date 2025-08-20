package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.JwtService;
import com.deporuis.auth.infraestructura.AuthController;
import com.deporuis.auth.infraestructura.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private com.deporuis.auth.aplicacion.CustomUserDetailsService userDetailsService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void login_success_returns_token() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setCodigo_universitario("u123");
        req.setPassword("secret");

        UserDetails ud = User.withUsername("u123").password("secret").roles("ADMINISTRADOR").build();

        when(userDetailsService.loadUserByUsername("u123")).thenReturn(ud);
        when(jwtService.generateToken(ud)).thenReturn("TOKEN-ABC");

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("TOKEN-ABC"));
    }

    @Test
    void login_bad_credentials_returns_401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setCodigo_universitario("wrong");
        req.setPassword("bad");

        doThrow(new BadCredentialsException("bad")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas"));
    }
}
