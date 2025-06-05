package com.deporuis.publicacion.infraestructura.dto;

import com.deporuis.Foto.dominio.Foto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicacionRequest {

//    titulo, descripcion,  lugar, fecha, duracion, foto
    @NotBlank(message = "El titulo de la publicacion es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripcion de la publicacion es obligatoria")
    private String descripcion;

    private String lugar;

    @NotBlank(message = "La fecha de la publicacion es obligatoria")
    private LocalDateTime fecha;

    @NotBlank(message = "La duracion de la publicacion es obligatoria")
    private String duracion;

    @NotBlank(message = "La foto de la publicacion es obligatoria")
    private Foto foto;
}
