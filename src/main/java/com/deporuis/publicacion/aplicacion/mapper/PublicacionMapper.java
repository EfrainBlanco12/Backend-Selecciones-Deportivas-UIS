package com.deporuis.publicacion.aplicacion.mapper;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse;

import java.util.List;
import java.util.Objects;

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
        dto.setTipoPublicacion(p.getTipoPublicacion() != null ? p.getTipoPublicacion().name() : null);
        dto.setFechaCreacion(p.getFecha());
        dto.setUsuarioModifico(p.getUsuarioModifico());
        dto.setFechaModificacion(p.getFechaModificacion());

        // Fotos
        List<FotoResponse> fotosDto = p.getFotos() == null ? List.of()
                : p.getFotos().stream()
                .filter(Objects::nonNull)
                .map(f -> new FotoResponse(
                        f.getIdFoto(),
                        f.getContenido(),
                        f.getTemporada(),
                        f.getIntegrante() != null ? f.getIntegrante().getIdIntegrante() : null,
                        f.getSeleccion() != null ? f.getSeleccion().getIdSeleccion() : null,
                        f.getPublicacion() != null ? f.getPublicacion().getIdPublicacion() : null
                ))
                .toList();
        dto.setFotos(fotosDto);

        // Selecciones (si la relación viene cargada en la entidad)
        if (p.getSelecciones() != null && !p.getSelecciones().isEmpty()) {
            var selDto = p.getSelecciones().stream()
                    .filter(Objects::nonNull)
                    .map(sp -> {
                        var s = sp.getSeleccion();
                        return s == null ? null
                                : new SeleccionPublicacionResponse(s.getIdSeleccion(), s.getNombreSeleccion());
                    })
                    .filter(Objects::nonNull)
                    .toList();
            dto.setIdSelecciones(selDto);
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
