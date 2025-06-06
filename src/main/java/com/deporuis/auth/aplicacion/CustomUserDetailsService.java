package com.deporuis.auth.aplicacion;

import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.infraestructura.LoginRepository;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginRepository loginRepository;
    private final IntegranteRepository integranteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Login login = loginRepository.findByCodigoUniversitario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Integrante integrante = integranteRepository.findByCodigoUniversitario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Integrante no encontrado"));

        String rol = integrante.getRol().toString();
        return new User(
                login.getCodigoUniversitario(),
                login.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + integrante.getRol().getNombreRol().toUpperCase())
                )
        );
    }
}
