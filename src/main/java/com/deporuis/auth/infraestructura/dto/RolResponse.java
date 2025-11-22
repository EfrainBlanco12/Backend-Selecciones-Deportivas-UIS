package com.deporuis.auth.infraestructura.dto;

import com.deporuis.auth.dominio.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {
    private Integer idRol;
    private String nombreRol;

    public RolResponse(Rol rol) {
        this.idRol = rol.getIdRol();
        this.nombreRol = rol.getNombreRol();
    }
}
