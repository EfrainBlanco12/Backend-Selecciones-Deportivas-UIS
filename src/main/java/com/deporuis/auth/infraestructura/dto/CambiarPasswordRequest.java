package com.deporuis.auth.infraestructura.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordRequest {
    
    @NotBlank(message = "El código universitario es obligatorio")
    private String codigo_universitario;
    
    @NotBlank(message = "La contraseña nueva es obligatoria")
    private String password_nueva;
}
