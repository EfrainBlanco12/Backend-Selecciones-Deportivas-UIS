package com.deporuis.auth.infraestructura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarLoginRequest {
    
    @NotNull(message = "El id del integrante es obligatorio")
    private Integer idIntegrante;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
