package com.deporuis.publicacion.infraestructura.dto;

import com.deporuis.publicacion.dominio.TipoPublicacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublicacionRequest {

//    titulo, descripcion,  lugar, fecha, duracion, tipoPublicacion, selecciones, fotos
    @NotBlank(message = "El titulo de la publicacion es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripcion de la publicacion es obligatoria")
    private String descripcion;

    private String lugar;

    @NotNull(message = "La fecha de la publicacion es obligatoria")
    private LocalDateTime fecha;

    @NotBlank(message = "La duracion de la publicacion es obligatoria")
    private String duracion;

    @NotNull(message = "El tipo de publicacion es obligatoria")
    private TipoPublicacion tipoPublicacion = TipoPublicacion.NOTICIA;

    @NotNull(message = "Debe elegir al menos una seleccion")
    private List<Integer> selecciones;

    @NotNull(message = "Debe elegir al menos una foto")
    private List<Integer> fotos;
}
