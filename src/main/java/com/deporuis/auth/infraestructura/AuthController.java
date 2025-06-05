package com.deporuis.auth.infraestructura;

import com.deporuis.auth.aplicacion.JwtService;
import com.deporuis.auth.infraestructura.dto.LoginRequest;
import com.deporuis.auth.infraestructura.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
