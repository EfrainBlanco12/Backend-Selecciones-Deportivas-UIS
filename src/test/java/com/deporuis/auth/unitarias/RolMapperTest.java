package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.mapper.RolMapper;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolMapperTest {

    @Test
    void toResponse_deberiaConvertirRolARolResponse() {
        // Arrange
        Rol rol = new Rol(1, "ADMINISTRADOR");

        // Act
        RolResponse resultado = RolMapper.toResponse(rol);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdRol());
        assertEquals("ADMINISTRADOR", resultado.getNombreRol());
    }

    @Test
    void toResponse_conRolNull_deberiaRetornarNull() {
        // Act
        RolResponse resultado = RolMapper.toResponse(null);

        // Assert
        assertNull(resultado);
    }

    @Test
    void toResponse_deberiaConvertirRolEntrenador() {
        // Arrange
        Rol rol = new Rol(2, "ENTRENADOR");

        // Act
        RolResponse resultado = RolMapper.toResponse(rol);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getIdRol());
        assertEquals("ENTRENADOR", resultado.getNombreRol());
    }

    @Test
    void toResponse_deberiaConvertirRolDeportista() {
        // Arrange
        Rol rol = new Rol(3, "DEPORTISTA");

        // Act
        RolResponse resultado = RolMapper.toResponse(rol);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.getIdRol());
        assertEquals("DEPORTISTA", resultado.getNombreRol());
    }
}
