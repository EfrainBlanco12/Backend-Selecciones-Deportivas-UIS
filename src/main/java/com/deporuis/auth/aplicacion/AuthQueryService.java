package com.deporuis.auth.aplicacion;

import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.infraestructura.LoginRepository;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthQueryService {

    private final IntegranteRepository integranteRepository;
    private final LoginRepository loginRepository;

    /**
     * Obtiene una lista paginada de integrantes que tienen login
     */
    @Transactional(readOnly = true)
    public Page<Integrante> obtenerIntegrantesConLogin(Pageable pageable) {
        return integranteRepository.findIntegrantesConLogin(pageable);
    }

    /**
     * Verifica si existe un login con el código universitario dado
     */
    @Transactional(readOnly = true)
    public boolean existeLoginPorCodigoUniversitario(String codigoUniversitario) {
        return loginRepository.findByCodigoUniversitario(codigoUniversitario).isPresent();
    }

    /**
     * Busca un login por código universitario
     */
    @Transactional(readOnly = true)
    public Optional<Login> buscarLoginPorCodigoUniversitario(String codigoUniversitario) {
        return loginRepository.findByCodigoUniversitario(codigoUniversitario);
    }
}
