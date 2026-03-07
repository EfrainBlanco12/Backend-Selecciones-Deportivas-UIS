package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.AuthService;
import com.deporuis.auth.aplicacion.JwtService;
import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.infraestructura.AuthController;
import com.deporuis.auth.infraestructura.dto.CambiarPasswordRequest;
import com.deporuis.auth.infraestructura.dto.LoginRequest;
import com.deporuis.auth.infraestructura.dto.VerificarPasswordRequest;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private com.deporuis.auth.aplicacion.CustomUserDetailsService userDetailsService;

    @MockitoBean
    private AuthService authService;

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

    @Test
    void verificarPassword_passwordCorrecta_debeRetornar200ConEsValidaTrue() throws Exception {
        // Arrange
        VerificarPasswordRequest req = new VerificarPasswordRequest();
        req.setCodigo_universitario("2200001");
        req.setPassword("password123");

        when(authService.verificarPassword("2200001", "password123")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(
                post("/auth/verificar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoUniversitario").value("2200001"))
                .andExpect(jsonPath("$.esValida").value(true));
    }

    @Test
    void verificarPassword_passwordIncorrecta_debeRetornar200ConEsValidaFalse() throws Exception {
        // Arrange
        VerificarPasswordRequest req = new VerificarPasswordRequest();
        req.setCodigo_universitario("2200001");
        req.setPassword("passwordIncorrecta");

        when(authService.verificarPassword("2200001", "passwordIncorrecta")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(
                post("/auth/verificar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoUniversitario").value("2200001"))
                .andExpect(jsonPath("$.esValida").value(false));
    }

    @Test
    void verificarPassword_usuarioNoExiste_debeRetornar200ConEsValidaFalse() throws Exception {
        // Arrange
        VerificarPasswordRequest req = new VerificarPasswordRequest();
        req.setCodigo_universitario("9999999");
        req.setPassword("password123");

        when(authService.verificarPassword("9999999", "password123")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(
                post("/auth/verificar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoUniversitario").value("9999999"))
                .andExpect(jsonPath("$.esValida").value(false));
    }

    @Test
    void verificarPassword_requestInvalido_debeRetornar400() throws Exception {
        // Arrange - Request con campos vacíos
        VerificarPasswordRequest req = new VerificarPasswordRequest();
        req.setCodigo_universitario("");
        req.setPassword("");

        // Act & Assert
        mockMvc.perform(
                post("/auth/verificar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarPassword_passwordActualizada_deberiaRetornar200ConMensaje() throws Exception {
        // Arrange
        CambiarPasswordRequest req = new CambiarPasswordRequest();
        req.setCodigo_universitario("2200001");
        req.setPassword_nueva("nuevaPassword123");

        Login loginActualizado = new Login();
        loginActualizado.setCodigoUniversitario("2200001");
        loginActualizado.setPassword("$2a$10$nuevaEncriptada");

        when(authService.cambiarPassword(eq("2200001"), eq("nuevaPassword123")))
                .thenReturn(loginActualizado);

        // Act & Assert
        mockMvc.perform(
                put("/auth/cambiar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoUniversitario").value("2200001"))
                .andExpect(jsonPath("$.mensaje").value("Contraseña actualizada exitosamente"));
    }

    @Test
    void cambiarPassword_usuarioNoExiste_deberiaRetornar404() throws Exception {
        // Arrange
        CambiarPasswordRequest req = new CambiarPasswordRequest();
        req.setCodigo_universitario("9999999");
        req.setPassword_nueva("nuevaPassword123");

        when(authService.cambiarPassword(eq("9999999"), eq("nuevaPassword123")))
                .thenThrow(new ResourceNotFoundException("No existe un login para el código universitario: 9999999"));

        // Act & Assert
        mockMvc.perform(
                put("/auth/cambiar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void cambiarPassword_requestInvalido_deberiaRetornar400() throws Exception {
        // Arrange - Request con campos vacíos
        CambiarPasswordRequest req = new CambiarPasswordRequest();
        req.setCodigo_universitario("");
        req.setPassword_nueva("");

        // Act & Assert
        mockMvc.perform(
                put("/auth/cambiar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarPassword_mismaPassword_deberiaRetornar400() throws Exception {
        // Arrange
        CambiarPasswordRequest req = new CambiarPasswordRequest();
        req.setCodigo_universitario("2200001");
        req.setPassword_nueva("password123");

        when(authService.cambiarPassword(eq("2200001"), eq("password123")))
                .thenThrow(new com.deporuis.auth.excepciones.MismaPasswordException(
                        "La nueva contraseña debe ser diferente a la contraseña actual"));

        // Act & Assert
        mockMvc.perform(
                put("/auth/cambiar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("La nueva contraseña debe ser diferente a la contraseña actual"));
    }
}
