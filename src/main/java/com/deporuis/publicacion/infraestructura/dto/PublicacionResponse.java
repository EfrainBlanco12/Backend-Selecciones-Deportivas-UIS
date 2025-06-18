package com.deporuis.publicacion.infraestructura.dto;

import com.deporuis.publicacion.dominio.TipoPublicacion;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PublicacionResponse {

    private Integer idPublicacion;

    private String tituloPublicacion;

    private String descripcion;

    private String lugar;

    private LocalDateTime fecha;

    private String duracion;

    private TipoPublicacion tipoPublicacion;

}
