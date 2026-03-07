package com.deporuis.posicion.infraestructura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PosicionRequest {

    @NotBlank(message = "El nombre de la posición es obligatorio.")
    private String nombrePosicion;

    @NotNull(message = "El ID del deporte es obligatorio.")
    private Integer idDeporte;
}
