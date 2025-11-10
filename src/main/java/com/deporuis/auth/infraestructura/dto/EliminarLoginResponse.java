package com.deporuis.auth.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EliminarLoginResponse {
    private String codigoUniversitario;
    private String mensaje;
}
