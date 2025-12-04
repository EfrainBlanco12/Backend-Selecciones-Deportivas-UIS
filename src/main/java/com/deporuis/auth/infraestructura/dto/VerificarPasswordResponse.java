package com.deporuis.auth.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificarPasswordResponse {
    
    private String codigoUniversitario;
    private boolean esValida;
}
