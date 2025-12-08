package com.deporuis.auth.aplicacion.helper;

import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.RolRepository;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolVerificarExistenciaService {

    @Autowired
    private RolRepository rolRepository;

    public Rol verificarRol(Integer id) {
        Optional<Rol> rol = rolRepository.findById(id);

        if (rol.isEmpty()) {
            throw new ResourceNotFoundException("Rol no existe");
        }

        return rol.get();
    }

    public void verificarPermisosCreacionIntegrantes(Integer idRolDestino) {
        Rol rolDestino = verificarRol(idRolDestino);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Extraemos las cadenas de autoridad: ROLE_ADMINISTRADOR, ROLE_ENTRENADOR, etc.
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMINISTRADOR"::equals);

        boolean isEntrenador = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ENTRENADOR"::equals);

        if (isAdmin) {
            return;
        }

        if (isEntrenador && rolDestino.getIdRol() == 3) {
            return;
        }

        // En cualquier otro caso, denegamos
        throw new AccessDeniedException(
                String.format("Un usuario con rol [%s] no puede gestionar un integrante con rol [%s]",
                        isAdmin ? "ADMINISTRADOR"
                                : isEntrenador ? "ENTRENADOR"
                                : "DEPORTISTA",
                        rolDestino));
    }
}
