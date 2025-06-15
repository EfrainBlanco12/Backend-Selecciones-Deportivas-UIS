package com.deporuis.publicacion.aplicacion.mapper;

import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;

public class PublicacionMapper {

    public static PublicacionResponse toResponse(Publicacion p) {
        return new PublicacionResponse(
                p.getIdPublicacion(),
                p.getTitulo(),
                p.getDescripcion(),
                p.getLugar(),
                p.getFecha(),
                p.getDuracion(),
                p.getTipoPublicacion()
        );
    }

    public static Publicacion requestToPublicacion(PublicacionRequest request) {
        return new Publicacion(
                request.getTitulo(),
                request.getDescripcion(),
                request.getLugar(),
                request.getFecha(),
                request.getDuracion(),
                request.getTipoPublicacion()
        );
    }
}