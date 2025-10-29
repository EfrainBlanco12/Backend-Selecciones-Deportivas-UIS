package com.deporuis.auth.aplicacion.mapper;

import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.dto.RolResponse;

public class RolMapper {

    public static RolResponse toResponse(Rol rol) {
        if (rol == null) {
            return null;
        }
        return new RolResponse(
                rol.getIdRol(),
                rol.getNombreRol()
        );
    }
}
