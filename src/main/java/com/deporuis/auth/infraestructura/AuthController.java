package com.deporuis.auth.infraestructura;

import com.deporuis.auth.aplicacion.AuthService;
import com.deporuis.auth.aplicacion.JwtService;
import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.infraestructura.dto.LoginRequest;
import com.deporuis.auth.infraestructura.dto.LoginResponse;
import com.deporuis.auth.infraestructura.dto.RegistrarLoginRequest;
import com.deporuis.auth.infraestructura.dto.RegistrarLoginResponse;
import com.deporuis.auth.infraestructura.dto.VerificarLoginResponse;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final com.deporuis.auth.aplicacion.CustomUserDetailsService userDetailsService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            System.out.println("TEXTO PLANO: " + loginRequest.getPassword());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCodigo_universitario(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCodigo_universitario());
            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    /**
     * Obtiene una lista paginada de integrantes que tienen login
     */
    @GetMapping("/private/integrantes-con-login")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<IntegranteResponse>> obtenerIntegrantesConLogin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Integrante> integrantes = authService.obtenerIntegrantesConLogin(pageable);
        Page<IntegranteResponse> response = integrantes.map(IntegranteMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Registra un nuevo login para un integrante
     */
    @PostMapping("/private/registrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RegistrarLoginResponse> registrarLogin(
            @RequestBody @Valid RegistrarLoginRequest request) {
        
        Login login = authService.registrarLogin(
                request.getIdIntegrante(), 
                request.getPassword()
        );
        
        RegistrarLoginResponse response = new RegistrarLoginResponse(
                login.getCodigoUniversitario(),
                "Login registrado exitosamente"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Verifica si un código universitario ya tiene login creado
     */
    @GetMapping("/verificar-login/{codigoUniversitario}")
    public ResponseEntity<VerificarLoginResponse> verificarLogin(
            @PathVariable String codigoUniversitario) {
        
        boolean tieneLogin = authService.existeLoginPorCodigoUniversitario(codigoUniversitario);
        
        VerificarLoginResponse response = new VerificarLoginResponse(
                codigoUniversitario,
                tieneLogin
        );
        
        return ResponseEntity.ok(response);
    }
}
