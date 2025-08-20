package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.helper.RolVerificarExistenciaService;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.RolRepository;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class RolVerificarExistenciaServiceTest {

    private RolRepository rolRepository;
    private RolVerificarExistenciaService service;

    @BeforeEach
    void setUp() {
        rolRepository = mock(RolRepository.class);
        service = new RolVerificarExistenciaService();
        org.springframework.test.util.ReflectionTestUtils.setField(service, "rolRepository", rolRepository);
    }

    @AfterEach
    void clearCtx() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void verificarRol_returns_entity_when_exists() {
        Rol r = new Rol(3, "DEPORTISTA");
        when(rolRepository.findById(3)).thenReturn(Optional.of(r));

        Rol result = service.verificarRol(3);

        assertEquals(3, result.getIdRol());
        assertEquals("DEPORTISTA", result.getNombreRol());
    }

    @Test
    void verificarRol_throws_when_not_found() {
        when(rolRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.verificarRol(100));
    }

    @Test
    void verificarPermisos_admin_can_manage_any_role() {
        when(rolRepository.findById(2)).thenReturn(Optional.of(new Rol(2, "ENTRENADOR")));
        var auth = new UsernamePasswordAuthenticationToken("admin", "x",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> service.verificarPermisosCreacionIntegrantes(2));
    }

    @Test
    void verificarPermisos_entrenador_can_manage_deportista() {
        when(rolRepository.findById(3)).thenReturn(Optional.of(new Rol(3, "DEPORTISTA")));
        var auth = new UsernamePasswordAuthenticationToken("coach", "x",
                List.of(new SimpleGrantedAuthority("ROLE_ENTRENADOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> service.verificarPermisosCreacionIntegrantes(3));
    }

    @Test
    void verificarPermisos_deportista_denied() {
        when(rolRepository.findById(3)).thenReturn(Optional.of(new Rol(3, "DEPORTISTA")));
        var auth = new UsernamePasswordAuthenticationToken("user", "x",
                List.of(new SimpleGrantedAuthority("ROLE_DEPORTISTA")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.verificarPermisosCreacionIntegrantes(3));
    }
}
