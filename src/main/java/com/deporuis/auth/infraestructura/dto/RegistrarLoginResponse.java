package com.deporuis.auth.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarLoginResponse {
    private String codigoUniversitario;
    private String mensaje;
}
