package com.deporuis.auth.aplicacion;

import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.excepciones.LoginYaExisteException;
import com.deporuis.auth.infraestructura.LoginRepository;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthQueryService authQueryService;
    private final IntegranteRepository integranteRepository;
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Delega la obtención de integrantes con login al servicio de consultas
     */
    public Page<Integrante> obtenerIntegrantesConLogin(Pageable pageable) {
        return authQueryService.obtenerIntegrantesConLogin(pageable);
    }

    /**
     * Verifica si existe un login para un código universitario
     */
    public boolean existeLoginPorCodigoUniversitario(String codigoUniversitario) {
        return authQueryService.existeLoginPorCodigoUniversitario(codigoUniversitario);
    }

    /**
     * Registra un nuevo login para un integrante
     */
    @Transactional
    public Login registrarLogin(Integer idIntegrante, String password) {
        // Buscar el integrante por ID
        Integrante integrante = integranteRepository.findById(idIntegrante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Integrante con id " + idIntegrante + " no encontrado"
                ));

        // Verificar que el integrante no tenga ya un login
        if (authQueryService.existeLoginPorCodigoUniversitario(integrante.getCodigoUniversitario())) {
            throw new LoginYaExisteException(
                    "Ya existe un login para el código universitario: " + integrante.getCodigoUniversitario()
            );
        }

        // Encriptar la contraseña
        String passwordEncriptada = passwordEncoder.encode(password);

        // Crear el login
        Login login = new Login();
        login.setCodigoUniversitario(integrante.getCodigoUniversitario());
        login.setPassword(passwordEncriptada);

        // Guardar el login
        return loginRepository.save(login);
    }
}
