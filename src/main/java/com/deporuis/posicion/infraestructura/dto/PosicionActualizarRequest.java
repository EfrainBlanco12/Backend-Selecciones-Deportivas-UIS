package com.deporuis.posicion.infraestructura.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PosicionActualizarRequest {

    @NotBlank(message = "El nombre de la posición no puede estar vacío")
    private String nombrePosicion;
}
