package com.deporuis.publicacion.aplicacion;

import com.deporuis.publicacion.aplicacion.mapper.PublicacionMapper;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublicacionQueryService {

    private final PublicacionRepository publicacionRepository;

    @Transactional(readOnly = true)
    public Page<PublicacionResponse> obtenerPublicacionesPaginadas(Integer page, Integer size) {
        var pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        var pubs = publicacionRepository.findByVisibilidadTrue(pageable);

        var content = pubs.getContent().stream()
                .map(p -> {
                    var dto = PublicacionMapper.toResponse(p);
                    dto.setIdSelecciones(obtenerSeleccionesDesdeRepo(p.getIdPublicacion()));
                    return dto;
                })
                .toList();

        return new PageImpl<>(content, pageable, pubs.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<PublicacionResponse> obtenerPublicacionesPorTipoPaginadas(TipoPublicacion tipo, Integer page, Integer size) {
        var pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        var pubs = publicacionRepository.findByVisibilidadTrueAndTipoPublicacion(tipo, pageable);

        var content = pubs.getContent().stream()
                .map(p -> {
                    var dto = PublicacionMapper.toResponse(p);
                    dto.setIdSelecciones(obtenerSeleccionesDesdeRepo(p.getIdPublicacion()));
                    return dto;
                })
                .toList();

        return new PageImpl<>(content, pageable, pubs.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PublicacionResponse obtenerPublicacion(Integer id) {
        var pub = publicacionRepository.findByIdPublicacionAndVisibilidadTrue(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Publicación no encontrada"));

        var dto = PublicacionMapper.toResponse(pub);
        dto.setIdSelecciones(obtenerSeleccionesDesdeRepo(id));
        return dto;
    }

    private java.util.List<com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse>
    obtenerSeleccionesDesdeRepo(Integer pubId) {
        return publicacionRepository.findSeleccionDtosByPublicacionId(pubId);
    }
}

