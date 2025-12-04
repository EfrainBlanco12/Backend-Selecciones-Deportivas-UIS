package com.deporuis.auth.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordResponse {
    private String codigoUniversitario;
    private String mensaje;
}
