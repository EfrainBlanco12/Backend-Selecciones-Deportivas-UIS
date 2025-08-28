// src/main/java/com/deporuis/publicacion/aplicacion/mapper/PublicacionMapper.java
package com.deporuis.publicacion.aplicacion.mapper;

import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;

import java.util.Objects;
import java.util.Optional;

public class PublicacionMapper {

    public static PublicacionResponse toResponse(Publicacion p) {
        if (p == null) return null;

        PublicacionResponse dto = new PublicacionResponse();
        dto.setIdPublicacion(p.getIdPublicacion());
        dto.setTitulo(p.getTitulo());
        dto.setDescripcion(p.getDescripcion());
        dto.setLugar(p.getLugar());
        dto.setFecha(p.getFecha());
        dto.setDuracion(p.getDuracion());
        dto.setVisibilidad(p.getVisibilidad());
        dto.setTipoPublicacion(Optional.ofNullable(p.getTipoPublicacion()).map(Enum::name).orElse(null));
        // Si tu DTO tiene fechaCreacion y solo existe "fecha" en el modelo:
        dto.setFechaCreacion(p.getFecha());

        // Fotos completas
        if (p.getFotos() != null) {
            dto.setFotos(p.getFotos().stream().map(FotoMapper::toResponse).toList());
        }

        // 🔹 IDs de las selecciones relacionadas (solo IDs, sin objeto)
        if (p.getSelecciones() != null) {
            dto.setIdSelecciones(
                    p.getSelecciones().stream()
                            .map(sp -> sp.getSeleccion() != null ? sp.getSeleccion().getIdSeleccion() : null)
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList()
            );
        }

        return dto;
    }

    public static Publicacion requestToPublicacion(PublicacionRequest request) {
        if (request == null) return null;
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
