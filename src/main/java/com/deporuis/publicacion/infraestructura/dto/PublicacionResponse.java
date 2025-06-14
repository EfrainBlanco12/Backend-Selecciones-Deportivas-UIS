package com.deporuis.publicacion.infraestructura.dto;

import com.deporuis.publicacion.dominio.TipoPublicacion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicacionResponse {

    private Integer idPublicacion;

    private String tituloPublicacion;

    private String descripcion;

    private String lugar;

    private LocalDateTime fecha;

    private String duracion;

    private TipoPublicacion tipoPublicacion;

    public PublicacionResponse(
            Integer idPublicacion,
            String tituloPublicacion,
            String descripcion,
            String lugar,
            LocalDateTime fecha,
            String duracion,
            TipoPublicacion tipoPublicacion) {
        this.idPublicacion = idPublicacion;
        this.tituloPublicacion = tituloPublicacion;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fecha = fecha;
        this.duracion = duracion;
        this.tipoPublicacion = tipoPublicacion;
    }
}
