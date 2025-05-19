package com.deporuis.publicacion.infraestructura.dto;

import lombok.Data;

@Data
public class PublicacionResponse {
    private Integer idPublicacion;

    private String tituloPublicacion;

    public PublicacionResponse(Integer idPublicacion, String tituloPublicacion) {
        this.tituloPublicacion = tituloPublicacion;
        this.idPublicacion = idPublicacion;
    }
}
