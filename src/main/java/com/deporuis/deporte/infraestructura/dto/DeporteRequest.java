package com.deporuis.deporte.infraestructura.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeporteRequest {
    @NotBlank(message = "El nombre del deporte es obligatorio.")
    private String nombreDeporte;

    @NotBlank(message = "La descripcion del deporte es obligatoria.")
    private String descripcionDeporte;
}
