package com.deporuis.Foto.infraestructura.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class FotoRequest {

    @NotNull(message = "Debe adjuntar al menos una imagen")
    private List<byte[]> contenidos;

    @NotNull(message = "Debe haber al menos una temporada")
    private List<Integer> temporadas;
}
