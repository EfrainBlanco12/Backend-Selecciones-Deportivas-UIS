package com.deporuis.Foto.infraestructura.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FotoRequest {

    @NotNull(message = "Debe adjuntar al menos una imagen")
    private byte[] contenido;

    @NotNull(message = "Debe haber al menos una temporada")
    private Integer temporada;
}
